
/**
 * OKBankNoSuchAlgorithmExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

package payment;

public class OKBankNoSuchAlgorithmExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1623195158346L;
    
    private payment.OKBankStub.OKBankNoSuchAlgorithmException faultMessage;

    
        public OKBankNoSuchAlgorithmExceptionException() {
            super("OKBankNoSuchAlgorithmExceptionException");
        }

        public OKBankNoSuchAlgorithmExceptionException(java.lang.String s) {
           super(s);
        }

        public OKBankNoSuchAlgorithmExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public OKBankNoSuchAlgorithmExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(payment.OKBankStub.OKBankNoSuchAlgorithmException msg){
       faultMessage = msg;
    }
    
    public payment.OKBankStub.OKBankNoSuchAlgorithmException getFaultMessage(){
       return faultMessage;
    }
}
    