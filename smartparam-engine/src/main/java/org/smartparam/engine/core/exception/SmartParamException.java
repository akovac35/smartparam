/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartparam.engine.core.exception;

/**
 * @author Przemek Hertel
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class SmartParamException extends RuntimeException {

    private static final int TO_STRING_LENGTH = 100;

    private String errorCode;

    public SmartParamException(Throwable t) {
        super(t.getMessage(), t);
    }

    public SmartParamException(String message) {
        super(message);
    }

    public SmartParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmartParamException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SmartParamException(String errorCode, Throwable t, String message) {
        super(message, t);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(TO_STRING_LENGTH);
        if (errorCode != null) {
            sb.append("[errorcode=").append(errorCode).append("] ");
        }
        sb.append(super.getMessage());
        return sb.toString();
    }
}
