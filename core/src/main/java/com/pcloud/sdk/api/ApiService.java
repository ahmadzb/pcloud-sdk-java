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

package com.pcloud.sdk.api;

import java.util.List;

public interface ApiService {

    Call<RemoteFolder> getFolder(long folderId);

    Call<List<FileEntry>> listFiles(RemoteFolder folder);

    Call<RemoteFolder> createFolder(long parentFolderId, String folderName);

    Call<RemoteFolder> deleteFolder(long folderId);

    Call<RemoteFolder> deleteFolder(RemoteFolder folder);

    Call<RemoteFolder> renameFolder(long folderId, String newFolderName);

    Call<RemoteFolder> renameFolder(RemoteFolder folder, String newFolderName);

    Call<RemoteFolder> moveFolder(long folderId, long toFolderId);

    Call<RemoteFolder> moveFolder(RemoteFolder folder, RemoteFolder toFolder);

    Call<RemoteFolder> copyFolder(long folderId, long toFolderId);

    Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder);

    Call<RemoteFile> createFile(RemoteFolder folder, String filename, Data data);

    Call<RemoteFile> createFile(long folderId, String filename, Data data);

    ApiServiceBuilder newBuilder();
}
