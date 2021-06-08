
/**
 * OKBankNoSuchPaddingExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

package payment;

public class OKBankNoSuchPaddingExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1623195158395L;
    
    private payment.OKBankStub.OKBankNoSuchPaddingException faultMessage;

    
        public OKBankNoSuchPaddingExceptionException() {
            super("OKBankNoSuchPaddingExceptionException");
        }

        public OKBankNoSuchPaddingExceptionException(java.lang.String s) {
           super(s);
        }

        public OKBankNoSuchPaddingExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public OKBankNoSuchPaddingExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(payment.OKBankStub.OKBankNoSuchPaddingException msg){
       faultMessage = msg;
    }
    
    public payment.OKBankStub.OKBankNoSuchPaddingException getFaultMessage(){
       return faultMessage;
    }
}
    