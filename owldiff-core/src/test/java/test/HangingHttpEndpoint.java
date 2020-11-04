/*
 * Copyright (c) 2012 Czech Technical University in Prague.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class HangingHttpEndpoint {

    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket();

            InetSocketAddress a = new InetSocketAddress("localhost", 80);

            s.bind(a, 1);


            while (true) {
                s.accept();

                try {
                    Thread.sleep(1000000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }


        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
