package com.crossfive.secularWorld;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.crossfive.framework.annotation.Action;
import com.crossfive.framework.annotation.Command;
import com.crossfive.framework.annotation.RequestParam;
import com.crossfive.framework.annotation.ServiceMeta;
import com.crossfive.framework.annotation.Syn;
import com.crossfive.framework.common.CustomApplication;
import com.crossfive.framework.common.dto.ParamDesc;
import com.crossfive.framework.common.init.Initializable;
import com.crossfive.framework.jdbc.dao.ApiMetaDao;
import com.crossfive.framework.jdbc.dao.ApiMetaDaoImpl;
import com.crossfive.framework.jdbc.domain.ApiMeta;
import com.crossfive.framework.util.ScanUtil;
import com.crossfive.framework.util.Utils;


public class ApiMetaInitializer implements Initializable {
	
	private static final Log logger = LogFactory.getLog(CustomApplication.class);
	
	private AbstractApplicationContext ac;
	private String packagePath;
	
	public ApiMetaInitializer(String packagePath) {
		this.packagePath = packagePath;
	}

	@Override
	public void init() throws Exception {
		if (ac == null) {
			logger.error("context is null！！！！！");
			throw new Exception();
		}
		
		String scanPath = packagePath;
		if (StringUtils.isEmpty(scanPath)) {
			logger.error("actionPackage path is null！！！！！");
			throw new Exception();
		}

		try {
			initHandleAction(scanPath);
		}catch (Exception e) {
			throw e;
		}
	}


	private void initHandleAction(String scanPath) throws Exception {
		Set<Class<?>> set = ScanUtil.getClasses(scanPath);
		// 此处可导入插件Plugin，目前尚未开发
		// 当前仅遍历路径下类文件
		for (Class<?> clazz : set) {
			initHandleAction(clazz);
		}
	}

	private void initHandleAction(Class<?> clazz) throws Exception {
//		if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
//			return;
//		}

		Action action = Utils.getAnnotation(clazz, Action.class);
		if (action == null) {
			return;
		}

		// 表示该方法只能同步访问
		boolean isSyn = false;
		Syn syn = clazz.getAnnotation(Syn.class);
		if (syn != null) {
			isSyn = syn.value();
		}

		createActionInvocation(clazz, isSyn);
	}

	private void createActionInvocation(Class<?> clazz, boolean isSyn) throws Exception {
		char[] firstLetter = new char[1];
		String type = clazz.getSimpleName();
		firstLetter[0] = type.charAt(0);
		String firstLetterStr = new String(firstLetter);
		type = type.replaceFirst(firstLetterStr, firstLetterStr.toLowerCase());

		ApiMetaDao apiMetaDao = ac.getBean(ApiMetaDaoImpl.class);
		List<ApiMeta> apiMetas = apiMetaDao.getApiMetasByType(type);
		Map<String, ApiMeta> apiMetasMap = new HashMap<String, ApiMeta>();
		if (apiMetas != null) {
			for (ApiMeta api : apiMetas) {
				apiMetasMap.put(api.getApiName(), api);
			}
		}


		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) || Modifier.isFinal(method.getModifiers())) {
				continue;
			}

			Command cmd = method.getAnnotation(Command.class);
			if (cmd == null) {
				continue;
			}

			ApiMeta apiMeta = apiMetasMap.remove(cmd.value());
			int version = cmd.version();
			// check version
			if (apiMeta != null) {
				if (apiMeta.getVersion() > version) {
					throw new Exception("Not newest api version!! please confirm");
				}
				if (apiMeta.getVersion() == version) {
					logger.info("api: "+cmd.value() + ", is newest version. Don't need to change.");
					continue;
				}
			}
			// build
			boolean exist = true;
			if (apiMeta == null) {
				apiMeta = new ApiMeta();
				exist = false;
			}
			
			apiMeta.setApiName(cmd.value());
			apiMeta.setApiType(type);
			apiMeta.setVersion(version);
			apiMeta.setUpdateTime(System.currentTimeMillis());
			apiMeta.setState(cmd.state().state());
			apiMeta.setDescription(cmd.description());
			
			ServiceMeta _serviceMeta = method.getAnnotation(ServiceMeta.class);
			if (_serviceMeta != null) {
				com.crossfive.framework.common.dto.ServiceMeta serviceMeta = new com.crossfive.framework.common.dto.ServiceMeta();
				serviceMeta.setRetry(_serviceMeta.retry());
				serviceMeta.setSyn(_serviceMeta.syn() | isSyn);
				serviceMeta.setTimeout(_serviceMeta.timeout());
				apiMeta.setServiceMeta(JSON.toJSONString(serviceMeta));
			}
			
			initParamMeta(apiMeta, method);
			if (exist) {
				apiMetaDao.update(apiMeta);
			}else {
				apiMetaDao.save(apiMeta);
			}
		}

	}

	private void initParamMeta(ApiMeta apiMeta, Method method) {
		List<ParamDesc> list = new ArrayList<ParamDesc>();
		Annotation[][] annoArr = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int count = 0; count < parameterTypes.length; count++) {
			Annotation[] anns = annoArr[count];
			RequestParam requestParam = null;
			
			for (Annotation ann : anns) {
				if (ann instanceof RequestParam) {
					requestParam = (RequestParam) ann;
					break;
				}
			}
			
			if (requestParam == null) {
				continue;
			}
			
			ParamDesc paramDesc = new ParamDesc();
			paramDesc.setName(requestParam.value());
			paramDesc.setOpt(requestParam.opt());
			paramDesc.setType(parameterTypes[count].getName());
			list.add(paramDesc);
		}
		apiMeta.setApiParams(JSON.toJSONString(list));
	}

	@Override
	public void setContext(AbstractApplicationContext context) {
		this.ac = context;
	}

}
