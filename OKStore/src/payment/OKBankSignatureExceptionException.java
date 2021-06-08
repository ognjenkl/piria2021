
/**
 * OKBankSignatureExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

package payment;

public class OKBankSignatureExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1623195158329L;
    
    private payment.OKBankStub.OKBankSignatureException faultMessage;

    
        public OKBankSignatureExceptionException() {
            super("OKBankSignatureExceptionException");
        }

        public OKBankSignatureExceptionException(java.lang.String s) {
           super(s);
        }

        public OKBankSignatureExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public OKBankSignatureExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(payment.OKBankStub.OKBankSignatureException msg){
       faultMessage = msg;
    }
    
    public payment.OKBankStub.OKBankSignatureException getFaultMessage(){
       return faultMessage;
    }
}
    