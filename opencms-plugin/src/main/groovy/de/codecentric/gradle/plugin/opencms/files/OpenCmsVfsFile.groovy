/*
 * Copyright 2015 codecentric AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsResourceType
import groovy.xml.MarkupBuilder
import org.gradle.api.Project

class OpenCmsVfsFile {
    StringWriter stringWriter = new StringWriter()
    MarkupBuilder builder = new MarkupBuilder(stringWriter)

    File file
    File meta
    Project project
    String type
    OpenCmsResourceType resourceType
    Date date = new Date()
    String path
    String rootPath

    def void createMetadata(String type, String path) {
        this.path = path
        this.type = type
        builder.doubleQuotes = true
        if (!meta.exists()) {
            prepareMetadata(resourceType.module.cms.username)
            writeMetadata()
            clearStringWriter()
        }
    }

    def clearStringWriter() {
        stringWriter.flush()
        stringWriter = new StringWriter()
        builder = new MarkupBuilder(stringWriter)
        builder.doubleQuotes = true
    }

    def writeMetadata() {
        meta.parentFile.mkdirs()
        meta.createNewFile()
        meta.text = stringWriter.toString();
    }

    def prepareMetadata(String user) {
        builder.file() {
            source(path)
            destination(path)
            type(type)
            uuidstructure(UUID.randomUUID())
            uuidresource(UUID.randomUUID())
            datelastmodified(now())
            userlastmodified(user)
            datecreated(now())
            usercreated(user)
            flags('0')
            addProperties()
            relations()
            accesscontrol()
        }
    }

    def addProperties() {
        builder.properties()
    }

    def cdata(String string) {
        String out = '<![CDATA[' + string + ']]>';
        builder.mkp.yieldUnescaped(out)
    }

    def now() {
        return date.format("EEE, d MMM yyyy HH:mm:ss z")
    }

    static String toFirstUpper(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
