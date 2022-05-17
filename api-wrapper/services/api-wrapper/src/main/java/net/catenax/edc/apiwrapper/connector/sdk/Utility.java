 /*
  *  Copyright (c) 2022 ZF Friedrichshafen AG
  *
  *  This program and the accompanying materials are made available under the
  *  terms of the Apache License, Version 2.0 which is available at
  *  https://www.apache.org/licenses/LICENSE-2.0
  *
  *  SPDX-License-Identifier: Apache-2.0
  *
  *  Contributors:
  *       ZF Friedrichshafen AG - Initial API and Implementation
  *
  */

package net.catenax.edc.apiwrapper.connector.sdk;

import okhttp3.MediaType;

public class Utility {
    public static final MediaType JSON = MediaType.get("application/json");

    private Utility() {}
}
