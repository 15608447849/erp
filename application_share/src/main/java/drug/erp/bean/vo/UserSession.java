package drug.erp.bean.vo;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:20
 * 用户信息
 */
public class UserSession {
    public String name;
    public String phone;

    @Override
    public String toString() {
        return "UserSession{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}