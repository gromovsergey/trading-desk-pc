package com.foros;

import com.foros.session.ServiceLocatorMock;
import com.foros.test.CurrentUserRule;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Rule;

public abstract class AbstractUnitTest {

    @Rule
    public CurrentUserRule currentUserRule = new CurrentUserRule();

    @Rule
    public ServiceLocatorMock serviceLocatorMock = ServiceLocatorMock.getInstance();

    protected File getTargetFolder() {
        Class<? extends AbstractUnitTest> clazz = getClass();
        String uri = clazz.getResource("").toExternalForm();
        String target = "/target/";
        int i = uri.indexOf(target);
        uri = uri.substring(0, i + target.length());
        try {
            return new File(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
