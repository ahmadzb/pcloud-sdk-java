/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pcloud.sdk.internal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pcloud.sdk.*;
import okio.BufferedSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

class RealRemoteFile extends RealRemoteEntry implements RemoteFile {

    @Expose
    @SerializedName("fileid")
    private long fileId;

    @Expose
    @SerializedName("contenttype")
    private String contentType;

    @Expose
    @SerializedName("size")
    private long size;

    @Expose
    @SerializedName("hash")
    private String hash;

    RealRemoteFile(ApiService apiService) {
        super(apiService);
    }

    @Override
    public long fileId() {
        return fileId;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public String hash() {
        return hash;
    }

    @Override
    public FileLink createFileLink(DownloadOptions options) throws IOException, ApiError {
        return ownerService().createFileLink(fileId, options).execute();
    }

    @Override
    public FileLink createFileLink() throws IOException, ApiError {
        return createFileLink(DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(contentType())
                .build());
    }

    @Override
    public RemoteFile asFile() {
        return this;
    }

    @Override
    public RemoteFile copy(RemoteFolder toFolder) throws IOException {
        return copy(toFolder, false);
    }

    @Override
    public RemoteFile copy(RemoteFolder toFolder, boolean overwrite) throws IOException {
        try {
            return ownerService().copyFile(this, toFolder, overwrite).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFile move(RemoteFolder toFolder) throws IOException {
        try {
            return ownerService().moveFile(this, toFolder).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFile rename(String newFilename) throws IOException {
        try {
            return ownerService().renameFile(this, newFilename).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public InputStream byteStream() throws IOException {
        return source().inputStream();
    }

    @Override
    public BufferedSource source() throws IOException {
        boolean success = false;
        Call<BufferedSource> call = ownerService().download(this);
        try {
            BufferedSource source = call.execute();
            success = true;
            return source;
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to download file.", apiError);
        } finally {
            if (!success) {
                call.cancel();
            }
        }
    }

    @Override
    public void download(DataSink sink, ProgressListener listener) throws IOException {
        DownloadOptions options = DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(contentType())
                .build();
        try {
            ownerService().createFileLink(this, options).execute().download(sink, listener);
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to download file.", apiError);
        }
    }

    @Override
    public void download(DataSink sink) throws IOException {
        download(sink, null);
    }

    static class InstanceCreator implements com.google.gson.InstanceCreator<RealRemoteFile> {

        private ApiService apiService;

        InstanceCreator(ApiService apiService) {
            this.apiService = apiService;
        }

        @Override
        public RealRemoteFile createInstance(Type type) {
            return new RealRemoteFile(apiService);
        }
    }
}
