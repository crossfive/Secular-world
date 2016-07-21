import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.crossfive.framework.common.dto.Result;
import com.crossfive.secularWorld.user.action.UserAction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { /*"classpath:provider/iov-service-provider.xml",*/
		"classpath:iov-service-consumer.xml"})
//		"classpath:service-context.xml" })
public class DubboTest {
	
	@Autowired
	private UserAction userAction;

	@Test
	public void testConsumer() {
		Result result = userAction.read(1);
		System.out.println(result);
	}
}
