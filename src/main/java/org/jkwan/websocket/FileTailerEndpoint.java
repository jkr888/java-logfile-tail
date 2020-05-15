/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.jkwan.websocket;

import java.io.File;
import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 */
@ServerEndpoint("/tailer")
public class FileTailerEndpoint implements Observer<Object> {

	private Observable<?> stream;
	private Disposable disposable;
	private String filename;
	private Async remote;
	int refreshRate = 5000;
	private String mode = "tail";

	public void init(String filename) {
		System.out.println("Init,file=" + filename + ",ms=" + refreshRate + ",mode=" + mode);
		this.filename = filename;
		FileTailer tailer = new FileTailer(new File(this.filename));
		if (mode.equalsIgnoreCase("all"))
			tailer.setEofOnly(false);
		this.stream = tailer.getStream(refreshRate);
		this.stream.subscribeOn(Schedulers.io()).subscribe(this);
	}

	@javax.websocket.OnOpen
	public void onOpen(Session session) throws IOException {
		this.remote = session.getAsyncRemote();
	}

	@OnMessage
	public String onMessage(String text) {

		String[] token = text.split("#");

		String filenameOrCmd = "";

		if (token[0] != null)
			filenameOrCmd = token[0];
		if (token.length > 1)
			refreshRate = Integer.parseInt(token[1]);
		if (token.length > 2)
			mode = token[2];

		// handle ping
		if (filenameOrCmd.equalsIgnoreCase("ping")) {
			return "pong";
		}

		if (!filenameOrCmd.equalsIgnoreCase(this.filename)) {
			init(filenameOrCmd);
		}
		
		StringBuilder resp = new StringBuilder();
		resp.append("<<< CMD:Filename=" + filenameOrCmd + ",ms=" + refreshRate + ",mode=" + mode);
		
		// get last 10
		try {
			StringBuilder last50 = TailUtil.getLastLines(this.filename, 10);
			resp.append(last50);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resp.toString();
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		if (this.disposable != null)
			this.disposable.dispose();
	}

	@Override
	public void onSubscribe(Disposable d) {
		this.disposable = d;
	}

	@Override
	public void onNext(Object t) {
		this.remote.sendText(t.toString());
	}

	@Override
	public void onError(Throwable e) {
	}

	@Override
	public void onComplete() {
	}
}
