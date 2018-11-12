/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.core.db;

import io.opentracing.contrib.jdbc.TracingConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class TracingDataSource implements DataSource {

  private final DataSource delegate;

  public TracingDataSource(DataSource delegate) {
    this.delegate = delegate;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return new TracingConnection(
        delegate.getConnection(), "che_db", null, true, Collections.emptySet());
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return new TracingConnection(
        delegate.getConnection(username, password), "che_db", null, true, Collections.emptySet());
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return delegate.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return delegate.isWrapperFor(iface);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return delegate.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    delegate.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    delegate.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return delegate.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return delegate.getParentLogger();
  }

  public static DataSource wrapWithTracingIfEnabled(DataSource dataSource) {
    return Boolean.valueOf(System.getenv("CHE_TRACING_ENABLED"))
        ? new TracingDataSource(dataSource)
        : dataSource;
  }
}
