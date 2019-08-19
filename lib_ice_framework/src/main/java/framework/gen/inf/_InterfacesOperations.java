// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.6.3
//
// <auto-generated>
//
// Generated from file `iceInterfaces.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package framework.gen.inf;

/**
 * 服务接口 interface
 **/
public interface _InterfacesOperations
{
    /**
     * 前后台交互
     * @param __current The Current object for the invocation.
     **/
    String accessService(IRequest request, Ice.Current __current);

    /**
     * 消息推送-服务端 / 客户端上线
     * @param __current The Current object for the invocation.
     **/
    void online(Ice.Identity identity, Ice.Current __current);

    /**
     * 消息推送-服务端 / 后端服务调用 - 向指定客户端发送消息
     * @param __current The Current object for the invocation.
     **/
    void sendMessageToClient(String identityName, String message, Ice.Current __current);
}
