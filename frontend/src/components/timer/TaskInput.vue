<template>
  <div class="flex items-center gap-3">
    <input
      :value="taskName"
      @input="$emit('update:taskName', ($event.target as HTMLInputElement).value)"
      type="text"
      placeholder="What are you working on?"
      :disabled="disabled"
      class="flex-1 px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition disabled:bg-gray-50 disabled:text-gray-400"
    />
    <select
      :value="selectedTagId ?? ''"
      @change="$emit('update:selectedTagId', ($event.target as HTMLSelectElement).value ? Number(($event.target as HTMLSelectElement).value) : null)"
      :disabled="disabled"
      class="px-3 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition disabled:bg-gray-50"
    >
      <option value="">No tag</option>
      <option v-for="tag in tags" :key="tag.id" :value="tag.id">
        {{ tag.name }}
      </option>
    </select>
  </div>
</template>

<script setup lang="ts">
import type { Tag } from '@/types'

defineProps<{
  taskName: string
  selectedTagId: number | null
  tags: Tag[]
  disabled?: boolean
}>()

defineEmits<{
  'update:taskName': [value: string]
  'update:selectedTagId': [value: number | null]
}>()
</script>
