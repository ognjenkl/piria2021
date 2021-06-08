
/**
 * OKBankBadPaddingExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

package payment;

public class OKBankBadPaddingExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1623195158364L;
    
    private payment.OKBankStub.OKBankBadPaddingException faultMessage;

    
        public OKBankBadPaddingExceptionException() {
            super("OKBankBadPaddingExceptionException");
        }

        public OKBankBadPaddingExceptionException(java.lang.String s) {
           super(s);
        }

        public OKBankBadPaddingExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public OKBankBadPaddingExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(payment.OKBankStub.OKBankBadPaddingException msg){
       faultMessage = msg;
    }
    
    public payment.OKBankStub.OKBankBadPaddingException getFaultMessage(){
       return faultMessage;
    }
}
    