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
import java.io.FileNotFoundException;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;

public class FileTailer {

    private final File file;
    private boolean eofOnly = true;

    public FileTailer(File file) {
        this.file = file;
    }

    public Observable<String> getStream(final long pollIntervalMs) {
        return Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                TailerListener listener = createListener(emitter);
                final Tailer tailer = new Tailer(file, listener, pollIntervalMs, isEofOnly());
                try {
                    tailer.run();
                } catch (Throwable e) {
                    emitter.onError(e);
                }
            }
        });
    }

    private TailerListenerAdapter createListener(final ObservableEmitter<String> emitter) {
        return new TailerListenerAdapter() {

            @Override
            public void fileRotated() {
                // ignore, just keep tailing
            }

            @Override
            public void handle(String line) {
                emitter.onNext(line);
            }

            @Override
            public void fileNotFound() {
                emitter.onError(new FileNotFoundException(file.toString()));
            }

            @Override
            public void handle(Exception ex) {
                emitter.onError(ex);
            }
        };
    }

	public boolean isEofOnly() {
		return eofOnly;
	}

	public void setEofOnly(boolean eofOnly) {
		this.eofOnly = eofOnly;
	}

}
