package junit.org.rapidpm.ddi.logging.v002;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.impl.StaticLoggerBinder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 11.05.16.
 */
public class LogginV002Test extends DDIBaseTest {

  @Test
  public void test001() throws Exception {
    final TestLoggerFactory testLoggerFactory = getTestLoggerFactory();
    final String s = new DemoClassV002StaticLoggingProxy()
        .withDelegator(new LoggingDemoClassV002())
        .doWork("YYY");
    System.out.println("s = " + s);

    DI.activateLogging(DemoClassV002.class);
    DI.activateLogging(LoggingDemoClassV002.class);

    final DemoClassV002 demoClassV002 = DI.activateDI(DemoClassV002.class);
    final String doWork = demoClassV002.doWork("XX");
    Assert.assertEquals("xxXX", doWork);
    System.out.println(doWork);

    final List<String> infoLogs = testLoggerFactory.getTestInMemoryLogger().getInfoLogs();
    System.out.println("infoLogs = " + infoLogs);
    Assert.assertEquals(2, infoLogs.size());
    Assert.assertEquals("delegator.doWork(txt) values - XX", infoLogs.get(1));
    infoLogs.clear();

  }

  @Test
  public void test002() throws Exception {

    final TestLoggerFactory testLoggerFactory = getTestLoggerFactory();

    DI.activateLogging(DemoClassV002.class.getPackage().getName());

    final DemoClassV002 demoClassV002 = DI.activateDI(DemoClassV002.class);
    final String doWork = demoClassV002.doWork("XX");
    Assert.assertEquals("xxXX", doWork);
    System.out.println(doWork);

    final List<String> infoLogs = testLoggerFactory.getTestInMemoryLogger().getInfoLogs();
    System.out.println("infoLogs = " + infoLogs);
    Assert.assertEquals(1, infoLogs.size());
    Assert.assertEquals("delegator.doWork(txt) values - XX", infoLogs.get(0));
    infoLogs.clear();
  }

  @NotNull
  private TestLoggerFactory getTestLoggerFactory() throws NoSuchFieldException, IllegalAccessException {
    final StaticLoggerBinder staticLoggerBinder = StaticLoggerBinder.getSingleton();
    final Class<? extends StaticLoggerBinder> staticLoggerBinderClass = staticLoggerBinder.getClass();
    //private final ILoggerFactory loggerFactory;
    final Field loggerFactoryField = staticLoggerBinderClass.getDeclaredField("loggerFactory");
    loggerFactoryField.setAccessible(true);
    final TestLoggerFactory testLoggerFactory = new TestLoggerFactory();
    loggerFactoryField.set(staticLoggerBinder, testLoggerFactory);

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    Assert.assertTrue(logger.isInfoEnabled());
    return testLoggerFactory;
  }


  public static class TestInMemoryLogger implements Logger {

    public List<String> getInfoLogs() {
      return infoLogs;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public boolean isTraceEnabled() {
      return false;
    }

    @Override
    public void trace(final String s) {

    }

    @Override
    public void trace(final String s, final Object o) {

    }

    @Override
    public void trace(final String s, final Object o, final Object o1) {

    }

    @Override
    public void trace(final String s, final Object... objects) {

    }

    @Override
    public void trace(final String s, final Throwable throwable) {

    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
      return false;
    }

    @Override
    public void trace(final Marker marker, final String s) {

    }

    @Override
    public void trace(final Marker marker, final String s, final Object o) {

    }

    @Override
    public void trace(final Marker marker, final String s, final Object o, final Object o1) {

    }

    @Override
    public void trace(final Marker marker, final String s, final Object... objects) {

    }

    @Override
    public void trace(final Marker marker, final String s, final Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled() {
      return false;
    }

    @Override
    public void debug(final String s) {

    }

    @Override
    public void debug(final String s, final Object o) {

    }

    @Override
    public void debug(final String s, final Object o, final Object o1) {

    }

    @Override
    public void debug(final String s, final Object... objects) {

    }

    @Override
    public void debug(final String s, final Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
      return false;
    }

    @Override
    public void debug(final Marker marker, final String s) {

    }

    @Override
    public void debug(final Marker marker, final String s, final Object o) {

    }

    @Override
    public void debug(final Marker marker, final String s, final Object o, final Object o1) {

    }

    @Override
    public void debug(final Marker marker, final String s, final Object... objects) {

    }

    @Override
    public void debug(final Marker marker, final String s, final Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled() {
      return true;
    }

    private static List<String> infoLogs = new ArrayList<>();

    @Override
    public void info(final String s) {
      infoLogs.add(s);
    }

    @Override
    public void info(final String s, final Object o) {
      infoLogs.add(s);
    }

    @Override
    public void info(final String s, final Object o, final Object o1) {
      infoLogs.add(s);
    }

    @Override
    public void info(final String s, final Object... objects) {
      infoLogs.add(s);
    }

    @Override
    public void info(final String s, final Throwable throwable) {
      infoLogs.add(s);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
      return true;
    }

    @Override
    public void info(final Marker marker, final String s) {
      infoLogs.add(s);
    }

    @Override
    public void info(final Marker marker, final String s, final Object o) {
      infoLogs.add(s);
    }

    @Override
    public void info(final Marker marker, final String s, final Object o, final Object o1) {
      infoLogs.add(s);
    }

    @Override
    public void info(final Marker marker, final String s, final Object... objects) {
      infoLogs.add(s);
    }

    @Override
    public void info(final Marker marker, final String s, final Throwable throwable) {
      infoLogs.add(s);
    }

    @Override
    public boolean isWarnEnabled() {
      return false;
    }

    @Override
    public void warn(final String s) {

    }

    @Override
    public void warn(final String s, final Object o) {

    }

    @Override
    public void warn(final String s, final Object... objects) {

    }

    @Override
    public void warn(final String s, final Object o, final Object o1) {

    }

    @Override
    public void warn(final String s, final Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
      return false;
    }

    @Override
    public void warn(final Marker marker, final String s) {

    }

    @Override
    public void warn(final Marker marker, final String s, final Object o) {

    }

    @Override
    public void warn(final Marker marker, final String s, final Object o, final Object o1) {

    }

    @Override
    public void warn(final Marker marker, final String s, final Object... objects) {

    }

    @Override
    public void warn(final Marker marker, final String s, final Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled() {
      return false;
    }

    @Override
    public void error(final String s) {

    }

    @Override
    public void error(final String s, final Object o) {

    }

    @Override
    public void error(final String s, final Object o, final Object o1) {

    }

    @Override
    public void error(final String s, final Object... objects) {

    }

    @Override
    public void error(final String s, final Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
      return false;
    }

    @Override
    public void error(final Marker marker, final String s) {

    }

    @Override
    public void error(final Marker marker, final String s, final Object o) {

    }

    @Override
    public void error(final Marker marker, final String s, final Object o, final Object o1) {

    }

    @Override
    public void error(final Marker marker, final String s, final Object... objects) {

    }

    @Override
    public void error(final Marker marker, final String s, final Throwable throwable) {

    }
  }


  public static class TestLoggerFactory implements ILoggerFactory {

    private static final TestInMemoryLogger testInMemoryLogger = new TestInMemoryLogger();

    public TestInMemoryLogger getTestInMemoryLogger() {
      return testInMemoryLogger;
    }

    @Override
    public Logger getLogger(final String s) {
      return testInMemoryLogger;
    }
  }

}
