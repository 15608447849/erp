package jdbc.define.option;

import jdbc.define.session.SessionManagerI;

/**
 * @Author: leeping
 * @Date: 2019/8/16 11:20
 */
public abstract class SessionOption<Manager extends SessionManagerI<S>, S>  implements DaoApi{
    private Manager manager;

    public SessionOption(Manager manager) {
        this.manager = manager;
    }

    public Manager getManager() {
        return this.manager;
    }

    protected S getSession() {
        return this.manager.getSession();
    }
}
