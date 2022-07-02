package su.reddot.domain.service.imageProcessing;

import org.im4java.core.IM4JavaException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import su.reddot.infrastructure.service.imageProcessing.ImageProcessor;

import java.io.IOException;

/**
 * @author Vitaliy Khludeev on 15.06.17.
 */
public class ImageProcessorTest {

	private ImageProcessor imageProcessor;

	@Before
	public void init() {
		imageProcessor = new ImageProcessor(new RabbitTemplate());
	}

	@Test
	public void testProcessImage() throws InterruptedException, IOException, IM4JavaException {
		imageProcessor.readFromQueue("/tmp/img.png");
	}
}
