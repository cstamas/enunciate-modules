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

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * ???
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 0.1
 */
public class RuleSetImpl
    extends RuleSetBase
{
    public void addRuleInstances(Digester digester) {
        digester.addSetProperties("enunciate/modules/wink-app");
        digester.addCallMethod("enunciate/modules/wink-app/init-param", "addServletInitParam", 2);
        digester.addCallParam("enunciate/modules/wink-app/init-param", 0, "name");
        digester.addCallParam("enunciate/modules/wink-app/init-param", 1, "value");
    }
}