/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package com.imoxion.sensems.server.nio.common;

import java.io.IOException;
import java.io.InputStream;

public class CountingBodyInputStream extends InputStream {

    private int count = 0;
    private int limit = -1;
    private int lastChar;
    private final InputStream in;
    private boolean isBody = false; // starting from header
    private boolean isEmptyLine = false;

    /**
     *
     * @param in
     *            InputStream to read from
     * @param limit
     *            the lines to read. -1 is used for no limits
     */
    public CountingBodyInputStream(InputStream in, int limit) {
        this.in = in;
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        if (limit != -1) {
            if (count <= limit) {
                int a = in.read();

                // check for empty line
                if (!isBody && isEmptyLine && lastChar == '\r' && a == '\n') {
                    // reached body
                    isBody = true;
                }

                if (lastChar == '\r' && a == '\n') {
                    // reset empty line flag
                    isEmptyLine = true;

                    if (isBody) {
                        count++;
                    }
                } else if (lastChar == '\n' && a != '\r') {
                    isEmptyLine = false;
                }

                lastChar = a;

                return a;
            } else {
                return -1;
            }
        } else {
            return in.read();
        }

    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void mark(int readlimit) {
        // not supported
    }

    @Override
    public void reset() throws IOException {
        // do nothing as mark is not supported
    }

    @Override
    public boolean markSupported() {
        return false;
    }

}
