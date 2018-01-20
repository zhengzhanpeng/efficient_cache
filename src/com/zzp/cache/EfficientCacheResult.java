/**
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.zzp.cache;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @author Albert
 * @create 2018-01-10 19:44
 */
public class EfficientCacheResult<Result, Key> implements CacheResult<Result, Key> {
    private final boolean IS_NOT_RETURN = true;
    private final ConcurrentHashMap<Key, Future<Result>> cache;

    private final Function<Key, Result> computeMethod;

    private EfficientCacheResult(Function<Key, Result> computeMethod) {
        this.computeMethod = computeMethod;
        this.cache = new ConcurrentHashMap<>();
    }

    public static <Result, Key> EfficientCacheResult createNeedComputeFunction(Function<Key, Result> computeFunction) {
        return new EfficientCacheResult<>(computeFunction);
    }

    @Override
    public Result compute(final Key key) {
        while (IS_NOT_RETURN) {
            Future<Result> resultFuture = cache.get(key);
            if (isNotExitResult(resultFuture)) {
                Callable<Result> putKeyComputeMethod = () -> computeMethod.apply(key);
                FutureTask<Result> runWhenResultFutureNull = new FutureTask<>(putKeyComputeMethod);
                resultFuture = cache.putIfAbsent(key, runWhenResultFutureNull);
                if (isNotExitResult(resultFuture)) {
                    resultFuture = runWhenResultFutureNull;
                    runWhenResultFutureNull.run();
                }
            }
            try {
                return resultFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNotExitResult(Future<Result> resultFuture) {
        return resultFuture == null;
    }
}
