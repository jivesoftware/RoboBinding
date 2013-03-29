/**
 * Copyright 2011 Cheng Wei, Robert Taylor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.robobinding.viewattribute.adapterview;

import org.robobinding.BindingContext;
import org.robobinding.attribute.AbstractPropertyAttribute;
import org.robobinding.attribute.StaticResourceAttribute;
import org.robobinding.viewattribute.AbstractReadOnlyPropertyViewAttribute;
import org.robobinding.viewattribute.ChildViewAttributeWithAttribute;
import org.robobinding.viewattribute.PropertyViewAttributeConfig;
import org.robobinding.viewattribute.ViewAttribute;

import android.widget.AdapterView;

/**
 * 
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
public class ItemLayoutAttribute implements ChildViewAttributeWithAttribute<AbstractPropertyAttribute>
{
	private final AdapterView<?> adapterView;
	protected final DataSetAdapter<?> dataSetAdapter;
	ViewAttribute layoutAttribute;
	
	public ItemLayoutAttribute(AdapterView<?> adapterView, DataSetAdapter<?> dataSetAdapter)
	{
		this.adapterView = adapterView;
		this.dataSetAdapter = dataSetAdapter;
	}

	@Override
	public void setAttribute(AbstractPropertyAttribute attribute)
	{
		AbstractPropertyAttribute propertyAttribute = attribute;
		if (propertyAttribute.isStaticResource())
			layoutAttribute = new StaticLayoutAttribute(propertyAttribute.asStaticResourceAttribute());
		else
		{
			DynamicLayoutAttribute dynamicLayoutAttribute = new DynamicLayoutAttribute();
			dynamicLayoutAttribute.initialize(
					new PropertyViewAttributeConfig<AdapterView<?>>(adapterView, propertyAttribute.asValueModelAttribute()));
			layoutAttribute = dynamicLayoutAttribute;
		}
	}
	
	@Override
	public void bindTo(BindingContext bindingContext)
	{
		layoutAttribute.bindTo(bindingContext);		
	}
	
	protected void updateLayoutId(int layoutId)
	{
		dataSetAdapter.setItemLayoutId(layoutId);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	class DynamicLayoutAttribute extends AbstractReadOnlyPropertyViewAttribute<AdapterView<?>, Integer>
	{
		public DynamicLayoutAttribute()
		{
			super(true);
		}
		
		@Override
		protected void valueModelUpdated(Integer newItemLayoutId)
		{
			updateLayoutId(newItemLayoutId);
			((AdapterView)view).setAdapter(dataSetAdapter);
		}
	}
	
	class StaticLayoutAttribute implements ViewAttribute
	{
		private StaticResourceAttribute attributeValue;

		public StaticLayoutAttribute(StaticResourceAttribute attributeValue)
		{
			this.attributeValue = attributeValue;
		}

		@Override
		public void bindTo(BindingContext bindingContext)
		{
			int itemLayoutId = attributeValue.getResourceId(bindingContext.getContext());
			updateLayoutId(itemLayoutId);
		}

		@Override
		public void preInitializeView(BindingContext bindingContext)
		{
		}
	}

}