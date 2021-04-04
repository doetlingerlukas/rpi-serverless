import com.openfaas.model.Request;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.openfaas.function.Handler;
import com.openfaas.model.IHandler;

import java.util.HashMap;

public class HandlerTest {
    @Test public void testAddition() {
        IHandler handler = new Handler();
        assertThat("4", is(handler.Handle(new Request("1,1,2", new HashMap<String, String>())).getBody()));
    }
}
