package global;

/**
 * @Author: leeping
 * @Date: 2019/8/5 10:06
 */
public class Holder<T> {
    public T value;
    public Holder() { }
    public Holder(T value) {
        this.value = value;
    }
}
