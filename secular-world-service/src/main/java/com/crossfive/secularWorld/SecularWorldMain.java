package com.crossfive.secularWorld;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.springframework.util.Log4jConfigurer;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.xml.sax.InputSource;

import com.crossfive.framework.common.CustomApplication;
import com.crossfive.framework.common.init.Initializable;

public class SecularWorldMain {

	public static void main(String[] args) throws Exception {
		Log4jConfigurer.initLogging("classpath:log4j.properties");

		try {
			String resolvedLocation = SystemPropertyUtils.resolvePlaceholders("classpath:proxool.xml");
			URL url = ResourceUtils.getURL(resolvedLocation);
			URLConnection uConn = url.openConnection();
			uConn.setUseCaches(false);
			InputStream stream = uConn.getInputStream();
			InputSource src = new InputSource(stream);
			src.setSystemId(url.toString());
			JAXPConfigurator.configure(src, false);
		}catch (Exception e) {
			throw e;
		}

		Initializable initializer = new ApiMetaInitializer("com.crossfive");
		CustomApplication application = new CustomApplication(initializer, new String[] {
				"classpath:applicationContext.xml", "classpath:provider/secular-world-provider.xml" });
		application.start();
		
	}
}
