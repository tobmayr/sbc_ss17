package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSBakeryUIService extends AbstractJMSService implements IBakeryUIService {

	private static Logger logger = LoggerFactory.getLogger(JMSTabletUIService.class);
	private Queue storageQueue;
	private MessageProducer storageProducer;

	public JMSBakeryUIService() {
		super(false, Session.AUTO_ACKNOWLEDGE);
		try {
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			storageProducer = session.createProducer(storageQueue);
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public boolean addIngredientsToStorage(List<Ingredient> ingredients) {
		for (Ingredient ingredient : ingredients) {
			if (!send(storageProducer, ingredient))
				return false;
		}
		
		return true;

	}

}
