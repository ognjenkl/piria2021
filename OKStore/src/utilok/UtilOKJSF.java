package utilok;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import model.Picture;

public class UtilOKJSF {

	/**
	 * Creates view message.
	 * 
	 * @param componentID
	 *            view component to show message
	 * @param messageKey
	 *            message key in locale language properties
	 */
	public static void jsfMessage(String componentID, String messageKey) {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		FacesContext.getCurrentInstance().addMessage(componentID,
				new FacesMessage(msgResourceBundle.getString(messageKey)));
	}

	public static void jsfMessage(String componentID, Severity severity, String messageKey, String detail) {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		FacesContext.getCurrentInstance().addMessage(componentID,
				new FacesMessage(severity, msgResourceBundle.getString(messageKey), detail));
	}
	
	/**
	 * Gets message from language resource bundle properties locale defined in
	 * faces-config.xhtml
	 * 
	 * @param messageKey
	 *            message key in locale language properties
	 * @return
	 */
	public static String getLangMessage(String messageKey) {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		return msgResourceBundle.getString(messageKey);

	}

	public static String getFilename(Part part) {
		// courtesy of BalusC : http://stackoverflow.com/a/2424824/281545
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

	public static String getArticleFilename(Part part) {
		String name = getFilename(part);
		SecureRandom random = new SecureRandom();
		return new BigInteger(50, random).toString() + name.substring(name.length() - 4);

	}

	// /**
	// * Uploads profile picture on server.
	// *
	// * @param profilePicName
	// * name of the profile picture to be saved.
	// * @param profilePicPart
	// * javax.servlet.http.Part object with info about picture.
	// * @param prop
	// * Properties file with info about file location, default name
	// * etc.
	// * @return profile picture name with extension.
	// */
	// public static String uploadProfilePic(String profilePicName, Part
	// profilePicPart, Properties prop) {
	// try (InputStream in = profilePicPart.getInputStream()) {
	// String dirPath = prop.getProperty("upload.profile.location");
	// File dir = new File(dirPath);
	// if (dir.exists()) {
	// String fileName = profilePicName + ".png";
	// // if no profile pic is specified
	// if (UtilOKJSF.getFilename(profilePicPart).equals("")) {
	// fileName = prop.getProperty("upload.profile.default.name");
	// System.out.println("naziv profilne slike: " + fileName);
	// return fileName;
	// }
	//
	// System.out.println("naziv profilne slike: " + fileName);
	// if (fileName.endsWith(".jpg") || fileName.endsWith(".png") ||
	// fileName.endsWith(".JPG")
	// || fileName.endsWith(".PNG")) {
	// String filePath = dirPath + File.separator + fileName;
	// File f = new File(filePath);
	// if (!f.exists()) {
	// Files.copy(in, new File(filePath).toPath());
	// System.out.println("Uploaded profile file: " + filePath);
	// return fileName;
	// } else {
	// System.out.println("Upload file \"" + fileName + "\" already exists");
	// return fileName;
	// }
	// } else {
	// System.out.println("Wrong upload file format!");
	// return null;
	// }
	// } else {
	// System.out.println("Directory \"" + dirPath + "\" for upload does not
	// exist");
	// return null;
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// System.out.print("Something is wrong with Part file: ");
	// System.out.println(profilePicName);
	// return null;
	// }
	// }

	public static double currencyFromCentesimal(long value) {
		return ((value * 1.0) / 100);
	}

	public static long currencyToCentesimal(double value) {
		double tempVal = new Double(roundDecimals(2, value));
		long retVal = (long) (tempVal * 100);
		return retVal;
	}

	/**
	 * Rounds double <code>valueToBeRounded</code> to
	 * <code>numOfDecimalPlaces</code> decimal places. Returns string
	 * representation of rounded number.
	 *
	 * @param numOfDecimalPlaces
	 * @param valueToBeRounded
	 * @return string representation ov <code>valueToBeRouonded</code>
	 */
	public static String roundDecimals(int numOfDecimalPlaces, double valueToBeRounded) {
		BigDecimal n = new BigDecimal(valueToBeRounded);
		return n.setScale(numOfDecimalPlaces, RoundingMode.HALF_UP).toPlainString();
	}

	/**
	 * Uploads article picture on server.
	 * 
	 * @param articlePicPart
	 * @return
	 */
	public static String uploadArticlePic(Part articlePicPart, Properties prop) {
		try (InputStream in = articlePicPart.getInputStream()) {
			String dirPath = prop.getProperty("picture.location.fileSystem");
			File dir = new File(dirPath);
			if (dir.exists()) {
				String fileName = UtilOKJSF.getArticleFilename(articlePicPart);
				System.out.println("Naziv slike artikla: " + fileName);
				if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".JPG")
						|| fileName.endsWith(".PNG")) {
					String filePath = dirPath + File.separator + fileName;
					File f = new File(filePath);
					if (!f.exists()) {
						Files.copy(in, new File(filePath).toPath());
						System.out.println("Uploaded article file: " + filePath);
						return fileName;
					} else {
						System.out.println("Upload file \"" + fileName + "\" already exists");
						return fileName;
					}
				} else {
					System.out.println("Wrong upload file format!");
					return null;
				}
			} else {
				System.out.println("Directory \"" + dirPath + "\" for upload does not exist");
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("Something is wrong with Part object: ");
			return null;
		}

	}

	public static Collection<Part> getAllParts(Part part) {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		try {
			return request.getParts().stream().filter(p -> part.getName().equals(p.getName()))
					.collect(Collectors.toList());
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 
	 * Uploads multiple article pictures on server on selected paht frome prop.
	 * 
	 * @param articlePicPart
	 * @param prop
	 * @return List of fileNames
	 */
	public static List<String> uploadArticlePicMultiple(Part articlePicPart, Properties prop) {
		// dirctory path on file system
		String fileSystemDirPath = prop.getProperty("picture.location.fileSystem");
		File dir = new File(fileSystemDirPath);
		List<String> fileNameList = null;
		if (articlePicPart != null) {
			Collection<Part> partCollection = getAllParts(articlePicPart);
			if (dir.exists() && partCollection.size() > 0) {
				String fileName = null;
				fileNameList = new ArrayList<>();
				for (Part part : partCollection) {
					try (InputStream in = part.getInputStream()) {
						// filename without path
						fileName = UtilOKJSF.getArticleFilename(articlePicPart);
						if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".JPG")
								|| fileName.endsWith(".PNG") || fileName.endsWith(".jpeg")
								|| fileName.endsWith(".JPEG")) {
							// full file name with path on file system
							String fileSystemFilePath = fileSystemDirPath + File.separator + fileName;
							File f = new File(fileSystemFilePath);
							if (!f.exists()) {
								// create file on file system
								Files.copy(in, new File(fileSystemFilePath).toPath());
								fileNameList.add(fileName);
							} else {
								System.out.println("Upload file \"" + fileName + "\" already exists");
							}
						} else {
							System.out.println("Wrong upload file format!");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.print("Something is wrong with Part object: ");
						return null;
					}
				}
				return fileNameList;
			} else {
				System.out.println("Directory \"" + fileSystemDirPath + "\" for upload does not exist");
				return null;
			}
		}
		return null;
	}

	/**
	 * Deletes picture on file system.
	 * 
	 * @param picture
	 * @param fileSystemDirPath
	 * @return true if deletion is successful
	 */
	public static Boolean deletePictureOnFileSystem(Picture picture, Properties prop) {
		String fileSystemDirPath = prop.getProperty("picture.location.fileSystem");
		String fileSystemFilePath = fileSystemDirPath + File.separator + picture.getName();
		File picOnFileSystem = new File(fileSystemFilePath);
		if (picOnFileSystem.exists()) {
			if (picOnFileSystem.delete()) {
				System.out.println("Successfully deleted file: " + fileSystemFilePath);
				return true;
			} else {
				System.out.println("Faied to delete file: " + fileSystemFilePath);
				return false;
			}
		} else {
			System.out.println("There is no file: " + fileSystemFilePath);
			return false;
		}
	}

	public static List<Picture> populetePictureList(List<String> fileNameList, Properties prop) {
		List<Picture> pictureList = new ArrayList<>();
		if (fileNameList != null && fileNameList.size() > 0) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			// dirctory path on file system
			String urlDirPath = prop.getProperty("picture.location.mapping");
			for (String fName : fileNameList) {
				String mappingFilePath = urlDirPath + "/" + fName;
				String serverPathWithProtocol = request.getRequestURL().toString().split(request.getContextPath())[0];
				// protocol://server:port + /OKStore + /articleImg + / + picture.jpg
				String urlFilePath = serverPathWithProtocol + request.getContextPath() + urlDirPath + "/" + fName;
				Picture picture = new Picture();
				picture.setLocation(mappingFilePath);
				picture.setName(fName);
				picture.setLocationURL(urlFilePath);
				// 1 to insert
				picture.setCrudFlag(1);
				
				pictureList.add(picture);
			}
		}
		return pictureList;
	}

}
