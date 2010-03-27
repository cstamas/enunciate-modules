/*
 * Copyright (C) 2010 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.enunciate.modules.wink.app;

import freemarker.template.TemplateException;
import org.apache.commons.digester.RuleSet;
import org.codehaus.enunciate.EnunciateException;
import org.codehaus.enunciate.apt.EnunciateClasspathListener;
import org.codehaus.enunciate.apt.EnunciateFreemarkerModel;
import org.codehaus.enunciate.modules.FreemarkerDeploymentModule;
import org.codehaus.enunciate.modules.SpecProviderModule;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Enunciate module to generate muck for an <a href="http://incubator.apache.org/wink">Apache Wink</a> application.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 0.1
 */
public class DeploymentModuleImpl
    extends FreemarkerDeploymentModule
    implements SpecProviderModule, EnunciateClasspathListener
{
//    private boolean jacksonAvailable = false;

    @Override
    public String getName() {
        return "wink-app";
    }

    @Override
    public boolean isDisabled() {
        if (super.isDisabled()) {
            return true;
        }
        else if (getModelInternal() != null && getModelInternal().getRootResources().isEmpty()) {
            debug("Module is disabled because there are no root resources");
            return true;
        }

        return false;
    }

    public boolean isJaxwsProvider() {
        return false;
    }

    public boolean isJaxrsProvider() {
        return true;
    }

    public URL getApplicationTemplateURL() {
        return getClass().getResource("application.fmt");
    }

    @Override
    public RuleSet getConfigurationRules() {
        return new RuleSetImpl();
    }

    public void onClassesFound(final Set<String> classes) {
//        jacksonAvailable |= classes.contains("org.codehaus.jackson.jaxrs.JacksonJsonProvider");
    }

    private boolean isUpToDate() {
        return getEnunciate().isUpToDateWithSources(getGenerateDir());
    }

    @Override
    public void initModel(final EnunciateFreemarkerModel model) {
        super.initModel(model);

        if (!isDisabled()) {
//            Map<String, String> contentTypes2Ids = model.getContentTypesToIds();
//
//            if (jacksonAvailable) {
//                contentTypes2Ids.put("application/json", "json"); //if we can load jackson, we've got json.
//            }
//            else {
//                debug("Couldn't find Jackson on the classpath, so it's assumed the REST endpoints aren't available in JSON format.");
//            }
//
//            for (RootResource resource : model.getRootResources()) {
//                for (ResourceMethod resourceMethod : resource.getResourceMethods(true)) {
//                    Map<String, Set<String>> subcontextsByContentType = new HashMap<String, Set<String>>();
//                    String subcontext = isUseSubcontext() ? getRestSubcontext() : "";
//                    debug("Resource method %s of resource %s to be made accessible at subcontext \"%s\".",
//                        resourceMethod.getSimpleName(), resourceMethod.getParent().getQualifiedName(), subcontext);
//                    subcontextsByContentType.put(null, new TreeSet<String>(Arrays.asList(subcontext)));
//                    resourceMethod.putMetaData("defaultSubcontext", subcontext);
//
//                    if (isUsePathBasedConneg()) {
//                        for (String producesMime : resourceMethod.getProducesMime()) {
//                            MediaType producesType = MediaType.valueOf(producesMime);
//
//                            for (Map.Entry<String, String> contentTypeToId : contentTypes2Ids.entrySet()) {
//                                MediaType type = MediaType.valueOf(contentTypeToId.getKey());
//                                if (producesType.isCompatible(type)) {
//                                    String id = '/' + contentTypeToId.getValue();
//                                    debug("Resource method %s of resource %s to be made accessible at subcontext \"%s\" because it produces %s/%s.",
//                                        resourceMethod.getSimpleName(), resourceMethod.getParent().getQualifiedName(), id, producesType.getType(), producesType.getSubtype());
//                                    String contentTypeValue = String.format("%s/%s", type.getType(), type.getSubtype());
//                                    Set<String> subcontextList = subcontextsByContentType.get(contentTypeValue);
//                                    if (subcontextList == null) {
//                                        subcontextList = new TreeSet<String>();
//                                        subcontextsByContentType.put(contentTypeValue, subcontextList);
//                                    }
//                                    subcontextList.add(id);
//                                }
//                            }
//                        }
//                    }
//
//                    resourceMethod.putMetaData("subcontexts", subcontextsByContentType);
//                }
//            }
        }
    }

    @Override
    public void doFreemarkerGenerate() throws EnunciateException, IOException, TemplateException {
        if (!isUpToDate()) {
            EnunciateFreemarkerModel model = getModel();
            processTemplate(getApplicationTemplateURL(), model);

//            Map<String, String> conentTypesToIds = model.getContentTypesToIds();
//            Properties mappings = new Properties();
//            for (Map.Entry<String, String> contentTypeToId : conentTypesToIds.entrySet()) {
//                mappings.put(contentTypeToId.getValue(), contentTypeToId.getKey());
//            }
//            File file = new File(getGenerateDir(), "media-type-mappings.properties");
//            FileOutputStream out = new FileOutputStream(file);
//            mappings.store(out, "JAX-RS media type mappings.");
//            out.flush();
//            out.close();
//
//            Map<String, String> ns2prefixes = model.getNamespacesToPrefixes();
//            mappings = new Properties();
//            for (Map.Entry<String, String> ns2prefix : ns2prefixes.entrySet()) {
//                mappings.put(ns2prefix.getKey() == null ? "" : ns2prefix.getKey(), ns2prefix.getValue());
//            }
//            file = new File(getGenerateDir(), "ns2prefix.properties");
//            out = new FileOutputStream(file);
//            mappings.store(out, "Namespace to prefix mappings.");
//            out.flush();
//            out.close();
        }
        else {
            info("Skipping generation; Everything appears up-to-date");
        }
    }

    @Override
    protected void doBuild() throws EnunciateException, IOException {
        super.doBuild();

        File webappDir = getBuildDir();
        webappDir.mkdirs();
        File webinf = new File(webappDir, "WEB-INF");
        getEnunciate().copyFile(new File(getGenerateDir(), "application"), new File(webinf, "application"));

//        BaseWebAppFragment webappFragment = new BaseWebAppFragment(getName());
//        webappFragment.setBaseDir(webappDir);
//        WebAppComponent servletComponent = new WebAppComponent();
//        servletComponent.setName("jersey");
//        servletComponent.setClassname(EnunciateJerseyServletContainer.class.getName());
//        TreeMap<String, String> initParams = new TreeMap<String, String>();
//        initParams.putAll(getServletInitParams());
//        if (!isUsePathBasedConneg()) {
//            initParams.put(JerseyAdaptedHttpServletRequest.FEATURE_PATH_BASED_CONNEG, Boolean.FALSE.toString());
//        }
//        if (isUseSubcontext()) {
//            initParams.put(JerseyAdaptedHttpServletRequest.PROPERTY_SERVLET_PATH, getRestSubcontext());
//        }
//        if (getResourceProviderFactory() != null) {
//            initParams.put(JerseyAdaptedHttpServletRequest.PROPERTY_RESOURCE_PROVIDER_FACTORY, getResourceProviderFactory());
//        }
//        servletComponent.setInitParams(initParams);
//
//        TreeSet<String> urlMappings = new TreeSet<String>();
//        for (RootResource rootResource : getModel().getRootResources()) {
//            for (ResourceMethod resourceMethod : rootResource.getResourceMethods(true)) {
//                String resourceMethodPattern = resourceMethod.getServletPattern();
//                for (Set<String> subcontextList : ((Map<String, Set<String>>) resourceMethod.getMetaData().get("subcontexts")).values()) {
//                    for (String subcontext : subcontextList) {
//                        String servletPattern;
//                        if ("".equals(subcontext)) {
//                            servletPattern = resourceMethodPattern;
//                        }
//                        else {
//                            servletPattern = subcontext + resourceMethodPattern;
//                        }
//
//                        if (urlMappings.add(servletPattern)) {
//                            debug("Resource method %s of resource %s to be made accessible by servlet pattern %s.",
//                                resourceMethod.getSimpleName(), resourceMethod.getParent().getQualifiedName(), servletPattern);
//                        }
//                    }
//                }
//            }
//        }
//
//        servletComponent.setUrlMappings(urlMappings);
//        webappFragment.setServlets(Arrays.asList(servletComponent));
//        getEnunciate().addWebAppFragment(webappFragment);
    }
}
