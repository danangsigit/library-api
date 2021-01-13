package test.technical.librarian.constant;

public class ErrorCode {

    public static final String SUCCESSFUL 			= "00";

    //Error code for Data Validation
    public static final String INVALID_DATA 		= "01";
    public static final String DUPLICATE_DATA 		= "02";
    public static final String INACTIVE_DATA 		= "03";
    public static final String DATA_NOTFOUND 		= "04";
    public static final String CANNOT_DELETE 		= "05";
    public static final String ALREADY_DELETED 		= "06";
    public static final String REQUIRED		 		= "07";

    //Error code Action
    public static final String UNAUTHORIZED			= "11";
    public static final String ACTION_NOT_PERMITTED	= "13";

    //Error code for javax.validation
    public static final String INVALID_REQUEST		= "40";
    public static final String INVALID_CAPTCHA		= "41";

    //Error code Others
    public static final String UPLOAD_ERROR 		= "90";
    public static final String FILE_NOT_ALLOWED		= "91";
    public static final String UNKNOWN_ERROR 		= "99";

}