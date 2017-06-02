package github.com.eventbus;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/2
 * Version  1.0
 * Description:
 */

public class TestBean {

    private String key;

    public TestBean(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "key='" + key + '\'' +
                '}';
    }
}
