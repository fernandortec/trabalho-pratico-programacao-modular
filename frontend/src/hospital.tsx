import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from 'react'

export type Patient = {
  id: string
  name: string
  document: string
  birthDate: string
  phone: string
  bloodType: string
}

export type Professional = {
  id: string
  name: string
  registration: string
  specialty: string
  unit: string
}

export type Appointment = {
  id: string
  patientId: string
  professionalId: string
  date: string
  time: string
  type: string
  status: 'Agendada' | 'Concluída' | 'Cancelada'
  notes: string
}

export type Room = {
  id: string
  name: string
  sector: string
  capacity: number
}

export type Hospitalization = {
  id: string
  patientId: string
  roomId: string
  admittedAt: string
  reason: string
  status: 'Internado' | 'Alta'
  dischargedAt?: string
}

export type HospitalData = {
  patients: Patient[]
  professionals: Professional[]
  appointments: Appointment[]
  rooms: Room[]
  hospitalizations: Hospitalization[]
}

const STORAGE_KEY = 'vida-hospital:data:v1'
const dateOffset = (days: number) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

export const initialData: HospitalData = {
  patients: [
    { id: 'p1', name: 'Mariana Costa', document: '284.***.***-09', birthDate: '1987-04-12', phone: '(11) 98842-1100', bloodType: 'O+' },
    { id: 'p2', name: 'Roberto Almeida', document: '390.***.***-41', birthDate: '1964-11-03', phone: '(11) 97731-2244', bloodType: 'A-' },
    { id: 'p3', name: 'Camila Ferreira', document: '128.***.***-72', birthDate: '1998-07-26', phone: '(11) 96654-3321', bloodType: 'AB+' },
    { id: 'p4', name: 'João Pedro Lima', document: '451.***.***-16', birthDate: '1975-02-18', phone: '(11) 95520-8841', bloodType: 'B+' },
  ],
  professionals: [
    { id: 'd1', name: 'Dra. Ana Ribeiro', registration: 'CRM 145.820', specialty: 'Cardiologia', unit: 'Ambulatório A' },
    { id: 'd2', name: 'Dr. Lucas Martins', registration: 'CRM 182.407', specialty: 'Clínica geral', unit: 'Ambulatório B' },
    { id: 'd3', name: 'Dra. Beatriz Souza', registration: 'CRM 163.091', specialty: 'Pediatria', unit: 'Ambulatório A' },
  ],
  appointments: [
    { id: 'a1', patientId: 'p2', professionalId: 'd1', date: dateOffset(-4), time: '09:30', type: 'Retorno', status: 'Concluída', notes: 'Acompanhamento de rotina.' },
    { id: 'a2', patientId: 'p1', professionalId: 'd1', date: dateOffset(1), time: '09:00', type: 'Consulta', status: 'Agendada', notes: 'Avaliação cardiológica.' },
    { id: 'a3', patientId: 'p3', professionalId: 'd3', date: dateOffset(1), time: '10:30', type: 'Consulta', status: 'Agendada', notes: 'Primeira consulta.' },
    { id: 'a4', patientId: 'p4', professionalId: 'd2', date: dateOffset(2), time: '14:00', type: 'Retorno', status: 'Agendada', notes: 'Revisão de exames.' },
    { id: 'a5', patientId: 'p1', professionalId: 'd2', date: dateOffset(-18), time: '11:00', type: 'Consulta', status: 'Concluída', notes: 'Atendimento clínico.' },
  ],
  rooms: [
    { id: 'r1', name: 'A-101', sector: 'Clínica médica', capacity: 2 },
    { id: 'r2', name: 'A-102', sector: 'Clínica médica', capacity: 2 },
    { id: 'r3', name: 'B-204', sector: 'Pediatria', capacity: 3 },
    { id: 'r4', name: 'UTI-03', sector: 'UTI', capacity: 1 },
  ],
  hospitalizations: [
    { id: 'h1', patientId: 'p2', roomId: 'r2', admittedAt: dateOffset(-2), reason: 'Observação clínica', status: 'Internado' },
    { id: 'h2', patientId: 'p4', roomId: 'r1', admittedAt: dateOffset(-35), reason: 'Recuperação pós-operatória', status: 'Alta', dischargedAt: dateOffset(-29) },
  ],
}

type HospitalContextValue = {
  data: HospitalData
  updateData: (change: (current: HospitalData) => HospitalData) => void
}

const HospitalContext = createContext<HospitalContextValue | null>(null)

export function HospitalProvider({ children }: { children: React.ReactNode }) {
  const [data, setData] = useState(initialData)

  useEffect(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY)
      if (saved) setData({ ...initialData, ...JSON.parse(saved) })
      else localStorage.setItem(STORAGE_KEY, JSON.stringify(initialData))
    } catch {
      localStorage.removeItem(STORAGE_KEY)
    }
  }, [])

  const updateData = useCallback((change: (current: HospitalData) => HospitalData) => {
    setData((current) => {
      const next = change(current)
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
      return next
    })
  }, [])

  return (
    <HospitalContext.Provider value={{ data, updateData }}>
      {children}
    </HospitalContext.Provider>
  )
}

export function useHospital() {
  const value = useContext(HospitalContext)
  if (!value) throw new Error('useHospital precisa estar dentro de HospitalProvider')
  return value
}

export function makeId(prefix: string) {
  return `${prefix}-${crypto.randomUUID()}`
}

export function formatDate(value: string) {
  if (!value) return '—'
  return new Date(`${value}T12:00:00`).toLocaleDateString('pt-BR')
}

export function patientName(data: HospitalData, id: string) {
  return data.patients.find((patient) => patient.id === id)?.name ?? 'Paciente removido'
}

export function professionalName(data: HospitalData, id: string) {
  return data.professionals.find((person) => person.id === id)?.name ?? 'Profissional removido'
}

export function roomName(data: HospitalData, id: string) {
  return data.rooms.find((room) => room.id === id)?.name ?? 'Quarto removido'
}

export function roomOccupancy(data: HospitalData, roomId: string) {
  return data.hospitalizations.filter(
    (stay) => stay.roomId === roomId && stay.status === 'Internado',
  ).length
}

export function PageHeading({
  eyebrow,
  title,
  description,
  action,
}: {
  eyebrow: string
  title: string
  description: string
  action?: React.ReactNode
}) {
  return (
    <div className="page-heading">
      <div>
        <p className="eyebrow">{eyebrow}</p>
        <h1>{title}</h1>
        <p className="page-description">{description}</p>
      </div>
      {action}
    </div>
  )
}

export function MetricCard({
  label,
  value,
  note,
  icon,
  accent = '',
}: {
  label: string
  value: string | number
  note: string
  icon: string
  accent?: string
}) {
  return (
    <article className="metric-card">
      <div className={`metric-icon ${accent}`}>{icon}</div>
      <div className="metric-copy">
        <span>{label}</span>
        <strong>{value}</strong>
        <small>{note}</small>
      </div>
    </article>
  )
}

export function SearchInput({
  value,
  onChange,
  placeholder,
}: {
  value: string
  onChange: (value: string) => void
  placeholder: string
}) {
  return (
    <label className="search-input">
      <span aria-hidden="true">⌕</span>
      <input
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
        aria-label={placeholder}
      />
    </label>
  )
}

export function StatusBadge({ children }: { children: string }) {
  const tone = children === 'Concluída' || children === 'Alta'
    ? 'success'
    : children === 'Cancelada'
      ? 'muted'
      : children === 'Internado'
        ? 'warning'
        : 'info'
  return <span className={`status-badge ${tone}`}>{children}</span>
}

export function Modal({
  title,
  description,
  onClose,
  children,
}: {
  title: string
  description?: string
  onClose: () => void
  children: React.ReactNode
}) {
  return (
    <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className="modal-card" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <div className="modal-heading">
          <div>
            <h2 id="modal-title">{title}</h2>
            {description && <p>{description}</p>}
          </div>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Fechar">×</button>
        </div>
        {children}
      </section>
    </div>
  )
}

export function initials(name: string) {
  return name.split(' ').slice(0, 2).map((part) => part[0]).join('').toUpperCase()
}

export function dayLabel(value: string) {
  const target = new Date(`${value}T12:00:00`)
  const today = new Date()
  const tomorrow = new Date()
  today.setHours(12, 0, 0, 0)
  tomorrow.setHours(12, 0, 0, 0)
  tomorrow.setDate(today.getDate() + 1)
  if (target.toDateString() === today.toDateString()) return 'Hoje'
  if (target.toDateString() === tomorrow.toDateString()) return 'Amanhã'
  return target.toLocaleDateString('pt-BR', { weekday: 'short', day: 'numeric', month: 'short' })
}
