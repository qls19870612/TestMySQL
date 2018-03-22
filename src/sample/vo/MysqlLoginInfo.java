package sample.vo;


import com.mysql.jdbc.StringUtils;

import java.io.Serializable;

/**
 * @author sims
 * @date 2018/2/5 12:07
 **/

public class MysqlLoginInfo implements Serializable {
    public String ip;
    public String username;
    public String pwd;

    public String toString()
    {
        return username + " : " + pwd + " : " + ip;
    }
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  MysqlLoginInfo))
        {
            return false;
        }
        MysqlLoginInfo info = (MysqlLoginInfo)obj;
       return stringIsEquals(ip, info.ip) && stringIsEquals(username, info.username) && stringIsEquals(pwd, info.pwd);
    }
    private boolean stringIsEquals(String a, String b)
    {
        if(StringUtils.isNullOrEmpty(a) && StringUtils.isNullOrEmpty(b))return true;
        return a.equals(b);
    }
}
