package com.suerte.lostandfound.vo.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
@Data
public class ProfileReq implements Serializable {
    private static final long serialVersionUID = 6143031250926579188L;
    @NotEmpty
    private String uid;
    @Length(min = 4)
    private String newAccount;

    private String oldAccount;
    @Email
    private String newEmail;
    @NotEmpty
    private String newQQ;
    @NotEmpty
    private String newPassword;
    @NotEmpty
    private String reNewPass;
}
