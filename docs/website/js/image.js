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

window.onload = function () {
    var imgs = document.getElementsByTagName("img");
    for (var i = 0, l = imgs.length; i < l; i++) {
        var sw = imgs[i].getAttribute("switch-src");
        if (sw) {
            (new Image()).src = sw;
            imgs[i].onmouseover = imgs[i].onmouseout = function () {
                var temp = this.src;
                this.src = this.getAttribute("switch-src");
                this.setAttribute("switch-src", temp);
            }
        }
    }
}
