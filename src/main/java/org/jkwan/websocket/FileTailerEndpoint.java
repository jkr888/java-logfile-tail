/*******

Copyright (c) 2017, 2019, Oracle Corporation and/or its affiliates. All rights reserved.

Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.

Subject to the condition set forth below, permission is hereby granted to any person obtaining
a copy of this software, associated documentation and/or data (collectively the "Software"),
free of charge and under any and all copyright rights in the Software, and any and all patent
rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified
Software as contributed to or provided by such licensor, or (ii) the Larger Works
(as defined below), to deal in both

(a) the Software, and

(b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with
the Software (each a "Larger Work" to which the Software is contributed by such licensors), without
restriction, including without limitation the rights to copy, create derivative works of, display,
perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made,
and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either
these or other terms.

This license is subject to the following condition:

The above copyright notice and either this complete permission notice or at a minimum a reference to
the UPL must be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ***
 *
 * @author johny.kwan@oracle.com 
 *         jkr888@gmail.com
 *         
 ***/
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
		resp.append("<<< CMD: filename=" + filenameOrCmd + ",ms=" + refreshRate + ",mode=" + mode + "\n");
		
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
