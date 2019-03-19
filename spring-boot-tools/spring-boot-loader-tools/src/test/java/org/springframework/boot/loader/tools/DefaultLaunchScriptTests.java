/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.loader.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.util.FileCopyUtils;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DefaultLaunchScript}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class DefaultLaunchScriptTests {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void loadsDefaultScript() throws Exception {
		DefaultLaunchScript script = new DefaultLaunchScript(null, null);
		String content = new String(script.toByteArray());
		assertThat(content, containsString("Spring Boot Startup Script"));
	}

	@Test
	public void initInfoProvidesCanBeReplaced() throws Exception {
		assertThatPlaceholderCanBeReplaced("initInfoProvides");
	}

	@Test
	public void initInfoShortDescriptionCanBeReplaced() throws Exception {
		assertThatPlaceholderCanBeReplaced("initInfoShortDescription");
	}

	@Test
	public void initInfoDescriptionCanBeReplaced() throws Exception {
		assertThatPlaceholderCanBeReplaced("initInfoDescription");
	}

	@Test
	public void initInfoChkconfigCanBeReplaced() throws Exception {
		assertThatPlaceholderCanBeReplaced("initInfoChkconfig");
	}

	@Test
	public void modeCanBeReplaced() throws Exception {
		assertThatPlaceholderCanBeReplaced("mode");
	}

	@Test
	public void useStartStopDaemonCanBeReplaced() throws Exception {
		assertThatPlaceholderCanBeReplaced("useStartStopDaemon");
	}

	@Test
	public void defaultForUseStartStopDaemonIsTrue() throws Exception {
		DefaultLaunchScript script = new DefaultLaunchScript(null, null);
		String content = new String(script.toByteArray());
		assertThat(content, containsString("USE_START_STOP_DAEMON=\"true\""));
	}

	@Test
	public void defaultForModeIsAuto() throws Exception {
		DefaultLaunchScript script = new DefaultLaunchScript(null, null);
		String content = new String(script.toByteArray());
		assertThat(content, containsString("MODE=\"auto\""));
	}

	@Test
	public void loadFromFile() throws Exception {
		File file = this.temporaryFolder.newFile();
		FileCopyUtils.copy("ABC".getBytes(), file);
		DefaultLaunchScript script = new DefaultLaunchScript(file, null);
		String content = new String(script.toByteArray());
		assertThat(content, equalTo("ABC"));
	}

	@Test
	public void expandVariables() throws Exception {
		File file = this.temporaryFolder.newFile();
		FileCopyUtils.copy("h{{a}}ll{{b}}".getBytes(), file);
		DefaultLaunchScript script = new DefaultLaunchScript(file,
				createProperties("a:e", "b:o"));
		String content = new String(script.toByteArray());
		assertThat(content, equalTo("hello"));
	}

	@Test
	public void expandVariablesMultiLine() throws Exception {
		File file = this.temporaryFolder.newFile();
		FileCopyUtils.copy("h{{a}}l\nl{{b}}".getBytes(), file);
		DefaultLaunchScript script = new DefaultLaunchScript(file,
				createProperties("a:e", "b:o"));
		String content = new String(script.toByteArray());
		assertThat(content, equalTo("hel\nlo"));
	}

	@Test
	public void expandVariablesWithDefaults() throws Exception {
		File file = this.temporaryFolder.newFile();
		FileCopyUtils.copy("h{{a:e}}ll{{b:o}}".getBytes(), file);
		DefaultLaunchScript script = new DefaultLaunchScript(file, null);
		String content = new String(script.toByteArray());
		assertThat(content, equalTo("hello"));
	}

	@Test
	public void expandVariablesWithDefaultsOverride() throws Exception {
		File file = this.temporaryFolder.newFile();
		FileCopyUtils.copy("h{{a:e}}ll{{b:o}}".getBytes(), file);
		DefaultLaunchScript script = new DefaultLaunchScript(file,
				createProperties("a:a"));
		String content = new String(script.toByteArray());
		assertThat(content, equalTo("hallo"));
	}

	@Test
	public void expandVariablesMissingAreUnchanged() throws Exception {
		File file = this.temporaryFolder.newFile();
		FileCopyUtils.copy("h{{a}}ll{{b}}".getBytes(), file);
		DefaultLaunchScript script = new DefaultLaunchScript(file, null);
		String content = new String(script.toByteArray());
		assertThat(content, equalTo("h{{a}}ll{{b}}"));
	}

	private void assertThatPlaceholderCanBeReplaced(String placeholder) throws Exception {
		DefaultLaunchScript script = new DefaultLaunchScript(null,
				createProperties(placeholder + ":__test__"));
		String content = new String(script.toByteArray());
		assertThat(content, containsString("__test__"));
	}

	private Map<?, ?> createProperties(String... pairs) {
		Map<Object, Object> properties = new HashMap<Object, Object>();
		for (String pair : pairs) {
			String[] keyValue = pair.split(":");
			properties.put(keyValue[0], keyValue[1]);
		}
		return properties;
	}

}