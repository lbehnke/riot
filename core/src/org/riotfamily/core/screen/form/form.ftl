<@template.set stylesheets=[
	"style/form.css", 
	"style/form-custom.css",
	"style/command.css", 
	"style/command-custom.css"
] />
<@template.extend file="../screen.ftl">
	<@template.block name="content" cache=false>
		<@riot.script src="/engine.js" />
		<@riot.script src="/util.js" />

		<@riot.script src="/interface/ListService.js" />
		<@riot.script src="riot-js/pager.js" />
		<@riot.script src="list.js" />
	
		<div class="main">
			<div id="form">${form}</div>
		</div>
		<div id="extra" class="extra">
			<div class="box">
				<div class="commands">
					<a class="action enabled" href="javascript:save()"><span class="icon saveButton"></span><span class="label"><@c.message "label.form.button.save">Save</@c.message></span></a>
					<div id="commands"></div>
				</div>
			</div>
		</div>
		<script type="text/javascript" language="JavaScript">
		
			<#--
			  - Submits the form by clicking on the submit button. 
			  - This function is invoked by the 'Save' button
			  - in the right-hand column.
			  -->
			function save(stayInForm) {
				var form = $$('form')[0];
				if (stayInForm) {
					form.insert(new Element('input', {type: 'hidden', name: 'focus', value: focusedElement}));
				}
				form.down('input.button-save').click();
			}

			<#--
			  - Registers a keypress listener on the given document. Note: 
			  - this function is also invoked by the setupcontent_callback of
			  - TinyMCE elements.
			  -->
			function registerKeyHandler(doc) {
				Event.observe(doc, 'keydown', function(e) {
				
					//we have to use code for KEY_CTRL(17) explicitly as it is not defined in Event
					if (e.keyCode == 17 || !e.ctrlKey)
						return;
					
					var key = String.fromCharCode(e.charCode ? e.charCode : e.keyCode).toUpperCase();
					if (key == 'S') {
						e.stop();					
						save(true);
					}
				});
			}
			
			registerKeyHandler(document);
			
			var list = new RiotList('${listStateKey}');
			list.renderFormCommands({objectId: <#if context.objectId??>'${context.objectId}'<#else>null</#if>}, 'commands');
		</script>
	</@template.block>
</@template.extend>