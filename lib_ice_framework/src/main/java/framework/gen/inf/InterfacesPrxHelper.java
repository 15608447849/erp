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
 * Provides type-specific helper functions.
 **/
public final class InterfacesPrxHelper extends Ice.ObjectPrxHelperBase implements InterfacesPrx
{
    private static final String __accessService_name = "accessService";

    public String accessService(IRequest request)
    {
        return accessService(request, null, false);
    }

    public String accessService(IRequest request, java.util.Map<String, String> __ctx)
    {
        return accessService(request, __ctx, true);
    }

    private String accessService(IRequest request, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        __checkTwowayOnly(__accessService_name);
        return end_accessService(begin_accessService(request, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_accessService(IRequest request)
    {
        return begin_accessService(request, null, false, false, null);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, java.util.Map<String, String> __ctx)
    {
        return begin_accessService(request, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, Ice.Callback __cb)
    {
        return begin_accessService(request, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_accessService(request, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, Callback_Interfaces_accessService __cb)
    {
        return begin_accessService(request, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, java.util.Map<String, String> __ctx, Callback_Interfaces_accessService __cb)
    {
        return begin_accessService(request, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, 
                                               IceInternal.Functional_GenericCallback1<String> __responseCb, 
                                               IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_accessService(request, null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, 
                                               IceInternal.Functional_GenericCallback1<String> __responseCb, 
                                               IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                               IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_accessService(request, null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, 
                                               java.util.Map<String, String> __ctx, 
                                               IceInternal.Functional_GenericCallback1<String> __responseCb, 
                                               IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_accessService(request, __ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_accessService(IRequest request, 
                                               java.util.Map<String, String> __ctx, 
                                               IceInternal.Functional_GenericCallback1<String> __responseCb, 
                                               IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                               IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_accessService(request, __ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_accessService(IRequest request, 
                                                java.util.Map<String, String> __ctx, 
                                                boolean __explicitCtx, 
                                                boolean __synchronous, 
                                                IceInternal.Functional_GenericCallback1<String> __responseCb, 
                                                IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_accessService(request, __ctx, __explicitCtx, __synchronous, 
                                   new IceInternal.Functional_TwowayCallbackArg1<String>(__responseCb, __exceptionCb, __sentCb)
                                       {
                                           public final void __completed(Ice.AsyncResult __result)
                                           {
                                               InterfacesPrxHelper.__accessService_completed(this, __result);
                                           }
                                       });
    }

    private Ice.AsyncResult begin_accessService(IRequest request, 
                                                java.util.Map<String, String> __ctx, 
                                                boolean __explicitCtx, 
                                                boolean __synchronous, 
                                                IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__accessService_name);
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__accessService_name, __cb);
        try
        {
            __result.prepare(__accessService_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            IRequest.__write(__os, request);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public String end_accessService(Ice.AsyncResult __iresult)
    {
        IceInternal.OutgoingAsync __result = IceInternal.OutgoingAsync.check(__iresult, this, __accessService_name);
        try
        {
            if(!__result.__wait())
            {
                try
                {
                    __result.throwUserException();
                }
                catch(Ice.UserException __ex)
                {
                    throw new Ice.UnknownUserException(__ex.ice_name(), __ex);
                }
            }
            IceInternal.BasicStream __is = __result.startReadParams();
            String __ret;
            __ret = __is.readString();
            __result.endReadParams();
            return __ret;
        }
        finally
        {
            if(__result != null)
            {
                __result.cacheMessageBuffers();
            }
        }
    }

    static public void __accessService_completed(Ice.TwowayCallbackArg1<String> __cb, Ice.AsyncResult __result)
    {
        framework.gen.inf.InterfacesPrx __proxy = (framework.gen.inf.InterfacesPrx)__result.getProxy();
        String __ret = null;
        try
        {
            __ret = __proxy.end_accessService(__result);
        }
        catch(Ice.LocalException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        catch(Ice.SystemException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        __cb.response(__ret);
    }

    private static final String __online_name = "online";

    public void online(Ice.Identity identity)
    {
        online(identity, null, false);
    }

    public void online(Ice.Identity identity, java.util.Map<String, String> __ctx)
    {
        online(identity, __ctx, true);
    }

    private void online(Ice.Identity identity, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        end_online(begin_online(identity, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity)
    {
        return begin_online(identity, null, false, false, null);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, java.util.Map<String, String> __ctx)
    {
        return begin_online(identity, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, Ice.Callback __cb)
    {
        return begin_online(identity, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_online(identity, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, Callback_Interfaces_online __cb)
    {
        return begin_online(identity, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, java.util.Map<String, String> __ctx, Callback_Interfaces_online __cb)
    {
        return begin_online(identity, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, 
                                        IceInternal.Functional_VoidCallback __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_online(identity, null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, 
                                        IceInternal.Functional_VoidCallback __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                        IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_online(identity, null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, 
                                        java.util.Map<String, String> __ctx, 
                                        IceInternal.Functional_VoidCallback __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_online(identity, __ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_online(Ice.Identity identity, 
                                        java.util.Map<String, String> __ctx, 
                                        IceInternal.Functional_VoidCallback __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                        IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_online(identity, __ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_online(Ice.Identity identity, 
                                         java.util.Map<String, String> __ctx, 
                                         boolean __explicitCtx, 
                                         boolean __synchronous, 
                                         IceInternal.Functional_VoidCallback __responseCb, 
                                         IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                         IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_online(identity, 
                            __ctx, 
                            __explicitCtx, 
                            __synchronous, 
                            new IceInternal.Functional_OnewayCallback(__responseCb, __exceptionCb, __sentCb));
    }

    private Ice.AsyncResult begin_online(Ice.Identity identity, 
                                         java.util.Map<String, String> __ctx, 
                                         boolean __explicitCtx, 
                                         boolean __synchronous, 
                                         IceInternal.CallbackBase __cb)
    {
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__online_name, __cb);
        try
        {
            __result.prepare(__online_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            Ice.Identity.__write(__os, identity);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_online(Ice.AsyncResult __iresult)
    {
        __end(__iresult, __online_name);
    }

    private static final String __sendMessageToClient_name = "sendMessageToClient";

    public void sendMessageToClient(String identityName, String message)
    {
        sendMessageToClient(identityName, message, null, false);
    }

    public void sendMessageToClient(String identityName, String message, java.util.Map<String, String> __ctx)
    {
        sendMessageToClient(identityName, message, __ctx, true);
    }

    private void sendMessageToClient(String identityName, String message, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        end_sendMessageToClient(begin_sendMessageToClient(identityName, message, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, String message)
    {
        return begin_sendMessageToClient(identityName, message, null, false, false, null);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, String message, java.util.Map<String, String> __ctx)
    {
        return begin_sendMessageToClient(identityName, message, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, String message, Ice.Callback __cb)
    {
        return begin_sendMessageToClient(identityName, message, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, String message, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_sendMessageToClient(identityName, message, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, String message, Callback_Interfaces_sendMessageToClient __cb)
    {
        return begin_sendMessageToClient(identityName, message, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, String message, java.util.Map<String, String> __ctx, Callback_Interfaces_sendMessageToClient __cb)
    {
        return begin_sendMessageToClient(identityName, message, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, 
                                                     String message, 
                                                     IceInternal.Functional_VoidCallback __responseCb, 
                                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_sendMessageToClient(identityName, message, null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, 
                                                     String message, 
                                                     IceInternal.Functional_VoidCallback __responseCb, 
                                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                     IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_sendMessageToClient(identityName, message, null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, 
                                                     String message, 
                                                     java.util.Map<String, String> __ctx, 
                                                     IceInternal.Functional_VoidCallback __responseCb, 
                                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_sendMessageToClient(identityName, message, __ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_sendMessageToClient(String identityName, 
                                                     String message, 
                                                     java.util.Map<String, String> __ctx, 
                                                     IceInternal.Functional_VoidCallback __responseCb, 
                                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                     IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_sendMessageToClient(identityName, message, __ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_sendMessageToClient(String identityName, 
                                                      String message, 
                                                      java.util.Map<String, String> __ctx, 
                                                      boolean __explicitCtx, 
                                                      boolean __synchronous, 
                                                      IceInternal.Functional_VoidCallback __responseCb, 
                                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                      IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_sendMessageToClient(identityName, 
                                         message, 
                                         __ctx, 
                                         __explicitCtx, 
                                         __synchronous, 
                                         new IceInternal.Functional_OnewayCallback(__responseCb, __exceptionCb, __sentCb));
    }

    private Ice.AsyncResult begin_sendMessageToClient(String identityName, 
                                                      String message, 
                                                      java.util.Map<String, String> __ctx, 
                                                      boolean __explicitCtx, 
                                                      boolean __synchronous, 
                                                      IceInternal.CallbackBase __cb)
    {
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__sendMessageToClient_name, __cb);
        try
        {
            __result.prepare(__sendMessageToClient_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            __os.writeString(identityName);
            __os.writeString(message);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_sendMessageToClient(Ice.AsyncResult __iresult)
    {
        __end(__iresult, __sendMessageToClient_name);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static InterfacesPrx checkedCast(Ice.ObjectPrx __obj)
    {
        return checkedCastImpl(__obj, ice_staticId(), InterfacesPrx.class, InterfacesPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __ctx The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static InterfacesPrx checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        return checkedCastImpl(__obj, __ctx, ice_staticId(), InterfacesPrx.class, InterfacesPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static InterfacesPrx checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        return checkedCastImpl(__obj, __facet, ice_staticId(), InterfacesPrx.class, InterfacesPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @param __ctx The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static InterfacesPrx checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        return checkedCastImpl(__obj, __facet, __ctx, ice_staticId(), InterfacesPrx.class, InterfacesPrxHelper.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param __obj The untyped proxy.
     * @return A proxy for this type.
     **/
    public static InterfacesPrx uncheckedCast(Ice.ObjectPrx __obj)
    {
        return uncheckedCastImpl(__obj, InterfacesPrx.class, InterfacesPrxHelper.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @return A proxy for this type.
     **/
    public static InterfacesPrx uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        return uncheckedCastImpl(__obj, __facet, InterfacesPrx.class, InterfacesPrxHelper.class);
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::inf::Interfaces"
    };

    /**
     * Provides the Slice type ID of this type.
     * @return The Slice type ID.
     **/
    public static String ice_staticId()
    {
        return __ids[1];
    }

    public static void __write(IceInternal.BasicStream __os, InterfacesPrx v)
    {
        __os.writeProxy(v);
    }

    public static InterfacesPrx __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            InterfacesPrxHelper result = new InterfacesPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }

    public static final long serialVersionUID = 0L;
}
