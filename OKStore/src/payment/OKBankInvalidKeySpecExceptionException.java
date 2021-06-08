
/**
 * OKBankInvalidKeySpecExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

package payment;

public class OKBankInvalidKeySpecExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1623195158407L;
    
    private payment.OKBankStub.OKBankInvalidKeySpecException faultMessage;

    
        public OKBankInvalidKeySpecExceptionException() {
            super("OKBankInvalidKeySpecExceptionException");
        }

        public OKBankInvalidKeySpecExceptionException(java.lang.String s) {
           super(s);
        }

        public OKBankInvalidKeySpecExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public OKBankInvalidKeySpecExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(payment.OKBankStub.OKBankInvalidKeySpecException msg){
       faultMessage = msg;
    }
    
    public payment.OKBankStub.OKBankInvalidKeySpecException getFaultMessage(){
       return faultMessage;
    }
}
    