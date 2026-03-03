import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Tag } from '@/types'
import { tagsApi } from '@/api/tags'

export const useTagsStore = defineStore('tags', () => {
  const tags = ref<Tag[]>([])

  async function fetchTags() {
    const { data } = await tagsApi.list()
    if (data.success && data.data) {
      tags.value = data.data
    }
  }

  async function createTag(name: string, color: string) {
    const { data } = await tagsApi.create({ name, color })
    if (data.success && data.data) {
      tags.value = [...tags.value, data.data]
    }
    return data
  }

  async function removeTag(id: number) {
    const { data } = await tagsApi.remove(id)
    if (data.success) {
      tags.value = tags.value.filter((t) => t.id !== id)
    }
    return data
  }

  return { tags, fetchTags, createTag, removeTag }
})
