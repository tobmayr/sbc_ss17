package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMUIService implements IUIService {
	private static Logger logger = LoggerFactory.getLogger(XVSMUIService.class);
	private final ContainerReference storageContainer;
	private Capi capi;

	public XVSMUIService(Capi capi) {
		this.capi=capi;
		storageContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
	}

	@Override
	public void addIngredientsToStorage(List<Ingredient> ingredients) {
		List<Entry> entries = new ArrayList<>();
		ingredients.forEach(element -> entries.add(new Entry(element)));
		try {
			capi.write(entries, storageContainer, RequestTimeout.TRY_ONCE, null);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}
	}

}