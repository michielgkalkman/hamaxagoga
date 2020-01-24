package org.taHjaj.wo.hamaxagoga.junit.support;

import static org.junit.jupiter.api.Assertions.fail;

/*
 * Copyright 2008 Michiel Kalkman
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeAll;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AbstractTestCase {

	@BeforeAll
	protected static void setUp() throws Exception {
	}

	protected String getTmpDirPath(final String tmpDir) {
		return new File(SystemUtils.getJavaIoTmpDir(),
				tmpDir).getAbsolutePath();
	}

	protected File getTmpDir(final String tmpDir) {
		return new File(SystemUtils.getJavaIoTmpDir(), tmpDir);
	}

	private Collection<String> listFiles(final File directory,
			final String[] extensions, final boolean recursive) {
		final Collection<String> files = new HashSet<String>();

		final String root = directory.getAbsolutePath();

		final Iterator<File> iterator = FileUtils.iterateFiles(directory,
				extensions, recursive);

		while (iterator.hasNext()) {
			final File file = iterator.next();

			files.add(StringUtils.removeStart(file.getAbsolutePath(), root));
		}

		return files;
	}

	protected void compareDirectories(
			final File directory1, final File directory2) {
		final Collection<String> files1 = listFiles(directory1,
				new String[] { "xml" }, true);
		final Collection<String> files2 = listFiles(directory2,
				new String[] { "xml" }, true);

		if (files1.equals(files2)) {
			boolean fFailure = false;

			for (final String file : files1) {
				final File file1 = new File(directory1, file);
				final File file2 = new File(directory2, file);

				try (
					Reader reader1 = new BufferedReader(new FileReader(file1));
					Reader reader2 = new BufferedReader(new FileReader(file2))) {

					if (!IOUtils.contentEquals(reader1, reader2)) {
						log.error("File " + file1.getAbsolutePath()
								+ " has another contents as "
								+ file2.getAbsolutePath());
						fFailure = true;
					}
				} catch (final FileNotFoundException fileNotFoundException) {
					
					final String message = "Error while comparing file contents for file "
							+ file;
					log.error(message, fileNotFoundException);
					fail(message);
				} catch (final IOException exception) {
					final String message = "Error while comparing file contents for file "
							+ file;
					log.error(message, exception);
					fail(message);
				}
			}

			if (fFailure) {
				fail("Some files had another content than was expected");
			}
		} else {
			logMissingFiles(directory1, directory2, files1, files2);
			logMissingFiles(directory2, directory1, files2, files1);

			fail("Generated files do not match expected files");
		}

	}

	private void logMissingFiles(final File directory1,
			final File directory2, final Collection<String> files1,
			final Collection<String> files2) {
		final Collection<String> missingFiles = CollectionUtils.subtract(
				files1, files2);

		for (final String missingFile : missingFiles) {
			final File file = new File(directory1, missingFile);
			log.error("File " + file.getName() + " occurs in "
					+ directory1.getAbsolutePath() + " but not in directory "
					+ directory2.getAbsolutePath());
		}
	}
}
