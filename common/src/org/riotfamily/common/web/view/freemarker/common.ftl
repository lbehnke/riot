<#assign locale = commonMacroHelper.getLocale() />
<#assign documentUri = commonMacroHelper.getOriginatingRequestUri() />
<#assign includeUri = commonMacroHelper.getPathWithinApplication() />
<#assign topLevelHandlerName = commonMacroHelper.getTopLevelHandlerName()?if_exists />

<#--
  - Macro that includes the given URI using a RequestDispatcher.
  -->
<#macro include uri="">
<#compress>
	<#if uri?is_sequence>
		<#list uri as item>
			${commonMacroHelper.include(item)}
		</#list>
	<#elseif uri?has_content>
		${commonMacroHelper.include(uri)}
	</#if>
</#compress>
</#macro>

<#macro setAttribute name value>
	${request.setAttribute(name, value)?if_exists}
</#macro>

<#function url uri>
	<#return commonMacroHelper.resolveAndEncodeUrl(uri) />
</#function>

<#function isExternalUrl url>
	<#return commonMacroHelper.isExternalUrl(url) />
</#function>

<#function absoluteUrl uri>
	<#return commonMacroHelper.getAbsoluteUrl(uri) />
</#function>

<#function urlForHandler handlerName attributes...>
	<#if attributes?size == 0>
		<#local uri = commonMacroHelper.getUrlForHandler(handlerName) />
	<#else>
		<#local attributes = attributes[0] />
		<#if attributes?is_hash>
			<#local uri = commonMacroHelper.getUrlForHandlerWithAttributes(handlerName, attributes) />
		<#else>
			<#local uri = commonMacroHelper.getUrlForHandlerWithAttribute(handlerName, attributes) />
		</#if>
	</#if>
	<#return url(uri) />
</#function>

<#function isHandler(handlerName)>
	<#return handlerName == topLevelHandlerName />
</#function>

<#function resource uri>
	<#return url(commonMacroHelper.addTimestamp(uri)) />
</#function>

<#function partition collection property>
	<#return commonMacroHelper.partition(collection, property) />
</#function>

<#function randomItem(collection)>
	<#local index = commonMacroHelper.random.nextInt(collection?size) />
	<#return collection[index] />
</#function>

<#function baseName url>
	<#return commonMacroHelper.baseName(url) />
</#function>

<#function fileExtension filename validExtension=[] defaultExtension="">
	<#return commonMacroHelper.getFileExtension(filename, validExtension, defaultExtension) />
</#function>

<#function formatByteSize bytes>
	<#return commonMacroHelper.formatByteSize(bytes) />
</#function>

<#function toTitleCase s>
	<#return commonMacroHelper.toTitleCase(s) />
</#function>


<#function getMessage code args=[] default=code>
	<#return commonMacroHelper.getMessage(code, args, default) />
</#function>

<#macro message code args=[] default=code>
${commonMacroHelper.getMessage(code, args, default)}
</#macro>
