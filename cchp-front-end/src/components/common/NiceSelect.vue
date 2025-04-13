<!-- 废弃 -->
<template>
  <div class="nice-select wide"
       :class="{ open: isOpen }"
       tabindex="0"
       @click="toggleOpen">
    <span class="current">{{ selectedOption }}</span>
    <ul class="list">
      <li v-for="option in options"
          :key="option.value"
          :data-value="option.value"
          :data-display="option.display"
          :class="['option', { selected: option.value === selectedValue, focus: option.value === focusedOption }]"
          @click="selectOption(option)"
          @mouseover="focusOption(option)">
        {{ option.display }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  props: {
    value: { // 双向绑定的 value
      type: [String, Number],
      default: ''
    },
    options: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      isOpen: false,      // 控制下拉菜单的显示和隐藏
      selectedOption: '', // 当前选中的选项
      focusedOption: '',  // 当前聚焦的选项
    };
  },
  computed: {
    selectedValue () {
      return this.value;
    }
  },
  methods: {
    toggleOpen () {
      this.isOpen = !this.isOpen;
    },
    closeSelect () {
      this.isOpen = false;
    },
    selectOption (option) {
      this.selectedOption = option.display;
      this.$emit('input', option.value); // 更新父组件的 value
      this.closeSelect();
    },
    focusOption (option) {
      this.focusedOption = option.value;
    }
  },
  watch: {
    value (newValue) {
      const selected = this.options.find(option => option.value === newValue);
      if (selected) {
        this.selectedOption = selected.display;
      }
    }
  },
};
</script>
