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

package org.sonatype.enunciate.modules.wink;

import freemarker.template.TemplateException;
import org.apache.commons.digester.RuleSet;
import org.apache.wink.server.internal.servlet.RestServlet;
import org.codehaus.enunciate.EnunciateException;
import org.codehaus.enunciate.apt.EnunciateFreemarkerModel;
import org.codehaus.enunciate.main.webapp.BaseWebAppFragment;
import org.codehaus.enunciate.main.webapp.WebAppComponent;
import org.codehaus.enunciate.modules.FreemarkerDeploymentModule;
import org.codehaus.enunciate.modules.SpecProviderModule;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Enunciate module to generate muck for an <a href="http://incubator.apache.org/wink">Apache Wink</a> application.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 0.1
 */
public class DeploymentModuleImpl
    extends FreemarkerDeploymentModule
    implements SpecProviderModule
{
    private final Map<String, String> servletInitParams = new HashMap<String, String>();

    @Override
    public String getName() {
        return "wink";
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

    public Map<String, String> getServletInitParams() {
        return servletInitParams;
    }

    public void addServletInitParam(final String name, final String value) {
        this.servletInitParams.put(name, value);
    }

    @Override
    public RuleSet getConfigurationRules() {
        return new RuleSetImpl();
    }

    private boolean isUpToDate() {
        return getEnunciate().isUpToDateWithSources(getGenerateDir());
    }

    @Override
    public void doFreemarkerGenerate() throws EnunciateException, IOException, TemplateException {
        if (isUpToDate()) {
            info("Skipping generation; Everything appears up-to-date");
            return;
        }

        EnunciateFreemarkerModel model = getModel();
        processTemplate(getApplicationTemplateURL(), model);
    }

    @Override
    protected void doBuild() throws EnunciateException, IOException {
        super.doBuild();

        File webappDir = getBuildDir();
        //noinspection ResultOfMethodCallIgnored
        webappDir.mkdirs();
        File webinf = new File(webappDir, "WEB-INF");
        getEnunciate().copyFile(new File(getGenerateDir(), "application"), new File(webinf, "application"));

        BaseWebAppFragment webappFragment = new BaseWebAppFragment(getName());
        webappFragment.setBaseDir(webappDir);
        WebAppComponent servletComponent = new WebAppComponent();
        servletComponent.setName("wink");
        servletComponent.setClassname(RestServlet.class.getName());
        TreeMap<String, String> initParams = new TreeMap<String, String>();
        initParams.putAll(getServletInitParams());
        initParams.put("applicationConfigLocation", "/WEB-INF/application");
//        initParams.put("propertiesLocation", "/WEB-INF/application.properties");
//        initParams.put("javax.ws.rs.Application", "some.package.ApplicationImpl");
        servletComponent.setInitParams(initParams);

        // TODO: Setup wink-admin servlet

        // TODO: Setup mapping

        webappFragment.setServlets(Arrays.asList(servletComponent));
        getEnunciate().addWebAppFragment(webappFragment);
    }
}
