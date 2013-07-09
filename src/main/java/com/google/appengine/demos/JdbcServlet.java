/**
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.google.appengine.demos;

import com.google.appengine.api.utils.SystemProperty;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;

public class JdbcServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException {
    res.setContentType("text/plain");

    String url = null;
    try {
      if (SystemProperty.environment.value() ==
          SystemProperty.Environment.Value.Production) {
        // Load the class that provides the new "jdbc:google:mysql://" prefix.
        Class.forName("com.mysql.jdbc.GoogleDriver");
        url = System.getProperty("cloudsql.url");
      } else {
        Class.forName("com.mysql.jdbc.Driver");
        url = System.getProperty("cloudsql.url.dev");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    try {
      Connection conn = DriverManager.getConnection(url);

      try {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SHOW DATABASES");
        while (rs.next()) {
          res.getWriter().println(rs.getString(1));
        }
        res.getWriter().println("-- done --");
      } finally {
        conn.close();
      }
    } catch (SQLException e) {
      res.getWriter().println("SQLException: " + e.getMessage());
    }

  }
}
