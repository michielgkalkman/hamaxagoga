package org.taHjaj.wo.hamaxagoga;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.log4j.Logger;
import org.apache.xerces.xs.XSModel;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.taHjaj.wo.hamaxagoga.generator.XSHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RandomXMLGenerator {
	private static final Logger logger = Logger
			.getLogger(RandomXMLGenerator.class);

	public RandomXMLGenerator() {
		super();
	}

	public void generate(final Params params, final String directory) {
		generate( params, new File(directory));
	}

	public void generate(final Params params, final File directory) {
		final XSModel xsmodel;
		try {
			xsmodel = XMLGenerator.getXsModel(params);
		} catch (final Exception e1) {
			throw new HamaxagogaException(e1);
		}

		if( params.isValidating()) {
			generateWithValidation( xsmodel, params, directory);
		} else {
			generateWithoutValidation(  xsmodel, params, directory);
		}
	}
	
	public void generate(final Params params, final File directory,
			final int count) {
		
		params.setCount( count);
		
		generate( params, directory);
	}

	public void generate(final Params params, final String directory,
			final int count) {
		generate( params, new File(directory), count);
	}

	public void generateWithValidation( final XSModel xsmodel, final Params params, final File directory) {
		directory.mkdirs();

		final int count = params.getCount();
		
		boolean fValid = true;
		final boolean fIgnoreValidationErrors = params.isIgnoringValidationErrors();
		
		final StringBuilder allParseErrorMsgs = new StringBuilder();
		
		for (int i = 0; (fValid || fIgnoreValidationErrors) && i < count; i++) {
			final String targetFile = i + ".xml";

			final File file = new File(directory, targetFile);
			final StringBuilder parseErrorMsgs = new StringBuilder();
			
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file);
				new XMLGenerator(params).createXMLDocument(outputStream,
						xsmodel);

				fValid = validate(params, file, parseErrorMsgs);
			} catch (FileNotFoundException e) {
				logger.error(e);
				fValid = false;
			} catch (HamaxagogaException e) {
				logger.error(e);
				fValid = false;
			} catch (SAXException e) {
				logger.error(e);
				fValid = false;
			} catch (IOException e) {
				logger.error(e);
				fValid = false;
			} catch (ParserConfigurationException e) {
				logger.error(e);
				fValid = false;
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
			if( !fValid) {
				if( allParseErrorMsgs.length() > 0) {
					allParseErrorMsgs.append( '\n');
				}
				allParseErrorMsgs.append( parseErrorMsgs);
			}
		}
		if (!fValid && !fIgnoreValidationErrors) {
			throw new HamaxagogaException(
					"Exception during XML generation: " + allParseErrorMsgs);
		} else {
			logger.error( allParseErrorMsgs);
		}
	}


	public void generateWithoutValidation( final XSModel xsmodel, final Params params, final File directory) {
		directory.mkdirs();

		final int count = params.getCount();

		for (int i = 0; i < count; i++) {
			final String targetFile = i + ".xml";

			final File file = new File(directory, targetFile);
			
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file);
				new XMLGenerator(params).createXMLDocument(outputStream,
						xsmodel);
			} catch ( final Throwable throwable) {
				final String errorMsg = "Error while generating XML document " + targetFile;
				logger.error( errorMsg, throwable);
				throw new HamaxagogaException( errorMsg, throwable);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}
	}

	private static class ThreadCommunicator {
		public boolean fValid = false;
	}

	public void generateWithParallelValidation(final Params params,
			final String directory) {
		generate(params, directory, params.getCount());
	}
	
	public void generateWithParallelValidation(final Params params,
			final File directory) {
		generate(params, directory, params.getCount());
	}
	
	public void generateWithParallelValidation(final Params params,
			final String targetDirectory, final int count) {
		generateWithParallelValidation( params, new File(targetDirectory), count);
	}
	
	public void generateWithParallelValidation(final Params params,
			final File directory, final int count) {
		directory.mkdirs();

		boolean fValid = true;
		final boolean fIgnoreValidationErrors = params.isIgnoringValidationErrors();

		final XSModel xsmodel;
		try {
			xsmodel = XMLGenerator.getXsModel(params);
		} catch ( final ClassNotFoundException classNotFoundException) {
			logger.error( classNotFoundException);
			throw new HamaxagogaException( classNotFoundException);
		} catch ( final InstantiationException instantiationException) {
			logger.error( instantiationException);
			throw new HamaxagogaException( instantiationException);
		} catch ( final IllegalAccessException illegalAccessException) {
			logger.error( illegalAccessException);
			throw new HamaxagogaException( illegalAccessException);
		}
		
		final StringBuilder allParseErrorMsgs = new StringBuilder();
		
		for (int i = 0; (fValid||fIgnoreValidationErrors) && i < count; i++) {
			final String targetFile = i + ".xml";

			final File file = new File(directory, targetFile);
			final StringBuilder currentFileParseErrorMsgs = new StringBuilder( file.getAbsolutePath()).append( '\n');
			
			OutputStream outputStream = null;
			TeeOutputStream teeOutputStream = null;
			PipedOutputStream pipedOutputStream = null;
			try {
				outputStream = new FileOutputStream(file);
				final PipedInputStream pipedInputStream = new PipedInputStream();
				pipedOutputStream = new PipedOutputStream();
				teeOutputStream = new TeeOutputStream(outputStream,
						pipedOutputStream);

				pipedInputStream.connect(pipedOutputStream);

				final ThreadCommunicator threadCommunicator = new ThreadCommunicator();

				final Runnable runnable = new Runnable() {
					public void run() {
						try {
							threadCommunicator.fValid = validate(params,
									pipedInputStream, currentFileParseErrorMsgs);
						} catch (ParserConfigurationException e) {
							logger.error(e);
						} catch (IOException e) {
							logger.error(e);
						} catch (SAXException e) {
							logger.error(e);
						} finally {
							if (pipedInputStream != null) {
								try {
									pipedInputStream.close();
								} catch ( final IOException exception) {
									// TODO Auto-generated catch block
									logger.error( "Exception while closing pipe", exception);
								}
							}
						}
					}
				};

				final Thread thread = new Thread(runnable);
				thread.start();

				new XMLGenerator(params).createXMLDocument(teeOutputStream,
						xsmodel);

				thread.join();

				fValid = threadCommunicator.fValid;
			} catch (InterruptedException e) {
				logger.error(e);
				fValid = false;
			} catch (FileNotFoundException e) {
				logger.error(e);
				fValid = false;
			} catch (HamaxagogaException e) {
				logger.error(e);
				fValid = false;
			} catch (SAXException e) {
				logger.error(e);
				fValid = false;
			} catch (IOException e) {
				logger.error(e);
				fValid = false;
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				if ( pipedOutputStream != null) {
					try {
						pipedOutputStream.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				if ( teeOutputStream != null) {
					try {
						teeOutputStream.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}

			if( !fValid) {
				if( allParseErrorMsgs.length() > 0) {
					allParseErrorMsgs.append( '\n');
				}
				allParseErrorMsgs.append( currentFileParseErrorMsgs);
			}
		}
		if (!fValid && !fIgnoreValidationErrors) {
			throw new HamaxagogaException(
					"Exception during XML generation: " + allParseErrorMsgs);
		}
	}

	private boolean validate(final Params params, final File file, final StringBuilder errorMsg)
			throws ParserConfigurationException, IOException, SAXException {

		if( logger.isDebugEnabled()) {
			logger.debug("Validating " + file.getAbsolutePath());
		}

		Reader reader = null;
		try {
			reader = new FileReader(file);
			final StringBuilder errorMsgs = new StringBuilder( file.getAbsolutePath()).append( ':');
			final boolean fValid = validate(params, reader, errorMsgs);
			
			if( !fValid) {
				errorMsg.append( errorMsgs);
			}
			
			return fValid;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch ( final IOException exception) {
				logger.error( "Error while closing reader " + exception);
			}
		}
	}

	private boolean validate(final Params params, final Reader reader, final StringBuilder errorMsg)
			throws ParserConfigurationException, IOException, SAXException {

		logger.debug("Validating ...");

		final StreamSource[] sources = new StreamSource[params.getXsds().size()];

		{
			int i = 0;
			for (final String xsd : params.getXsds()) {
				sources[i] = new StreamSource(xsd);
				i++;
			}
		}

		final SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = factory.newSchema(sources);

		/** Setup SAX parser for schema validation. */
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);
		SAXParser parser = parserFactory.newSAXParser();

		final XSHandler handler = new XSHandler();

		parser.parse(new InputSource(reader), handler);

		for( final String parseErrorMsg : handler.getParseErrors()) {
			errorMsg.append( '\n').append( parseErrorMsg);
		}

		return handler.isValid();
	}

	private boolean validate(final Params params, final InputStream inputStream, final StringBuilder errorMsg)
			throws ParserConfigurationException, IOException, SAXException {

		logger.debug("Validating ...");

		final StreamSource[] sources = new StreamSource[params.getXsds().size()];

		{
			int i = 0;
			for (final String xsd : params.getXsds()) {
				sources[i] = new StreamSource(xsd);
				i++;
			}
		}

		final SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = factory.newSchema(sources);

		/** Setup SAX parser for schema validation. */
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);
		SAXParser parser = parserFactory.newSAXParser();

		final XSHandler handler = new XSHandler();

		parser.parse(inputStream, handler);

		for( final String parseErrorMsg : handler.getParseErrors()) {
			errorMsg.append( '\n').append( parseErrorMsg);
		}
		
		return handler.isValid();
	}
}
