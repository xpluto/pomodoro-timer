<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-800">Statistics</h1>

    <!-- Weekly Summary Cards -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white rounded-xl border border-gray-200 p-4 text-center">
        <div class="text-3xl font-bold text-red-500">{{ weeklySummary.totalPomodoros }}</div>
        <div class="text-sm text-gray-500 mt-1">Pomodoros</div>
      </div>
      <div class="bg-white rounded-xl border border-gray-200 p-4 text-center">
        <div class="text-3xl font-bold text-red-500">{{ weeklySummary.totalHours }}</div>
        <div class="text-sm text-gray-500 mt-1">Hours</div>
      </div>
      <div class="bg-white rounded-xl border border-gray-200 p-4 text-center">
        <div class="text-3xl font-bold text-red-500">{{ weeklySummary.avgPerDay }}</div>
        <div class="text-sm text-gray-500 mt-1">Avg/Day</div>
      </div>
    </div>

    <!-- Date Navigation -->
    <div class="flex items-center justify-between">
      <button @click="prevWeek" class="px-3 py-1 text-gray-600 hover:text-red-500 transition">
        &larr; Previous
      </button>
      <span class="text-sm font-medium text-gray-700">{{ weekLabel }}</span>
      <button @click="nextWeek" :disabled="isCurrentWeek" class="px-3 py-1 text-gray-600 hover:text-red-500 transition disabled:opacity-30">
        Next &rarr;
      </button>
    </div>

    <!-- Daily Bar Chart -->
    <div class="bg-white rounded-2xl border border-gray-200 p-6">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">Daily Pomodoros</h2>
      <Bar v-if="barChartData.labels.length > 0" :data="barChartData" :options="barChartOptions" />
      <div v-else class="text-gray-400 text-sm text-center py-8">No data for this week</div>
    </div>

    <!-- Tag Breakdown Pie Chart -->
    <div class="bg-white rounded-2xl border border-gray-200 p-6">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">Tag Breakdown</h2>
      <div class="max-w-xs mx-auto">
        <Pie v-if="pieChartData.labels.length > 0" :data="pieChartData" :options="pieChartOptions" />
        <div v-else class="text-gray-400 text-sm text-center py-8">No tag data for this week</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Bar, Pie } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  ArcElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import { statsApi, type WeeklyDataPoint, type DailyDetail } from '@/api/stats'

ChartJS.register(Title, Tooltip, Legend, BarElement, ArcElement, CategoryScale, LinearScale)

const weekOffset = ref(0)
const weeklyData = ref<WeeklyDataPoint[]>([])
const todayDetail = ref<DailyDetail | null>(null)

const weekStart = computed(() => {
  const d = new Date()
  d.setDate(d.getDate() - d.getDay() + 1 + weekOffset.value * 7)
  d.setHours(0, 0, 0, 0)
  return d
})

const weekEnd = computed(() => {
  const d = new Date(weekStart.value)
  d.setDate(d.getDate() + 6)
  return d
})

const isCurrentWeek = computed(() => weekOffset.value >= 0)

const weekLabel = computed(() => {
  const fmt = (d: Date) => d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
  return `${fmt(weekStart.value)} - ${fmt(weekEnd.value)}`
})

const weeklySummary = computed(() => {
  const totalPomodoros = weeklyData.value.reduce((s, d) => s + d.completedCount, 0)
  const totalMinutes = weeklyData.value.reduce((s, d) => s + d.totalMinutes, 0)
  return {
    totalPomodoros,
    totalHours: (totalMinutes / 60).toFixed(1),
    avgPerDay: weeklyData.value.length > 0 ? (totalPomodoros / 7).toFixed(1) : '0'
  }
})

const barChartData = computed(() => {
  const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  const data = days.map((_, i) => {
    const d = new Date(weekStart.value)
    d.setDate(d.getDate() + i)
    const dateStr = d.toISOString().slice(0, 10)
    const stat = weeklyData.value.find((s) => s.date === dateStr)
    return stat?.completedCount ?? 0
  })
  return {
    labels: days,
    datasets: [{
      label: 'Pomodoros',
      data,
      backgroundColor: '#FF6347',
      borderRadius: 6
    }]
  }
})

const barChartOptions = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: {
    y: { beginAtZero: true, ticks: { stepSize: 1 } }
  }
}

const pieChartData = computed(() => {
  if (!todayDetail.value?.tagBreakdown) {
    return { labels: [] as string[], datasets: [{ data: [] as number[], backgroundColor: [] as string[] }] }
  }
  const tb = todayDetail.value.tagBreakdown
  return {
    labels: tb.map((t) => t.tagName),
    datasets: [{
      data: tb.map((t) => t.count),
      backgroundColor: tb.map((t) => t.tagColor)
    }]
  }
})

const pieChartOptions = {
  responsive: true,
  plugins: { legend: { position: 'bottom' as const } }
}

function formatDate(d: Date): string {
  return d.toISOString().slice(0, 10)
}

async function fetchData() {
  try {
    const [weeklyRes, dailyRes] = await Promise.all([
      statsApi.weekly(formatDate(weekStart.value)),
      statsApi.daily(formatDate(new Date()))
    ])
    if (weeklyRes.data.success && weeklyRes.data.data) {
      weeklyData.value = weeklyRes.data.data
    }
    if (dailyRes.data.success && dailyRes.data.data) {
      todayDetail.value = dailyRes.data.data
    }
  } catch (error) {
    console.error('Failed to fetch stats:', error)
    weeklyData.value = []
    todayDetail.value = null
  }
}

function prevWeek() {
  weekOffset.value--
}

function nextWeek() {
  if (weekOffset.value < 0) {
    weekOffset.value++
  }
}

watch(weekOffset, fetchData)
onMounted(fetchData)
</script>
