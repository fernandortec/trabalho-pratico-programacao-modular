import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { formatDate, makeId, Modal, PageHeading, patientName, professionalName, SearchInput, StatusBadge, useHospital, type Appointment } from '../hospital'

export const Route = createFileRoute('/consultas')({ component: Consultas })

const today = () => new Date().toLocaleDateString('en-CA')
const tomorrow = () => {
  const date = new Date()
  date.setDate(date.getDate() + 1)
  return date.toLocaleDateString('en-CA')
}
const filters = ['Todas', 'Agendada', 'Concluída', 'Cancelada'] as const

function Consultas() {
  const { data, updateData } = useHospital()
  const [query, setQuery] = useState('')
  const [filter, setFilter] = useState<(typeof filters)[number]>('Todas')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Appointment | null>(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const appointments = [...data.appointments]
    .filter((item) => filter === 'Todas' || item.status === filter)
    .filter((item) => `${patientName(data, item.patientId)} ${professionalName(data, item.professionalId)} ${item.type}`.toLowerCase().includes(query.toLowerCase()))
    .sort((a, b) => {
      const statusOrder = { Agendada: 0, Concluída: 1, Cancelada: 2 }
      const byStatus = statusOrder[a.status] - statusOrder[b.status]
      return byStatus || (a.status === 'Agendada'
        ? `${a.date}${a.time}`.localeCompare(`${b.date}${b.time}`)
        : `${b.date}${b.time}`.localeCompare(`${a.date}${a.time}`))
    })

  function openForm(appointment?: Appointment) {
    setEditing(appointment ?? null)
    setError('')
    setOpen(true)
  }

  function saveAppointment(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const appointment: Appointment = {
      id: editing?.id ?? makeId('a'),
      patientId: String(form.get('patientId')),
      professionalId: String(form.get('professionalId')),
      date: String(form.get('date')),
      time: String(form.get('time')),
      type: String(form.get('type')),
      status: editing?.status ?? 'Agendada',
      notes: String(form.get('notes')).trim(),
    }
    const conflict = data.appointments.find((item) => item.id !== appointment.id && item.status === 'Agendada' && item.date === appointment.date && item.time === appointment.time && item.professionalId === appointment.professionalId)
    if (conflict) {
      return setError('Este profissional já tem uma consulta agendada neste dia e horário.')
    }
    const patientConflict = data.appointments.some((item) => item.id !== appointment.id && item.status === 'Agendada' && item.patientId === appointment.patientId && item.date === appointment.date && item.time === appointment.time)
    if (patientConflict) return setError('Este paciente já tem uma consulta agendada neste horário.')
    const now = new Date()
    const currentTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    if (appointment.date < today() || appointment.date === today() && appointment.time < currentTime) return setError('Escolha uma data e horário futuros.')
    updateData((current) => ({
      ...current,
      appointments: editing
        ? current.appointments.map((item) => item.id === appointment.id ? appointment : item)
        : [appointment, ...current.appointments],
    }))
    setNotice(editing ? 'Consulta atualizada.' : 'Consulta agendada com sucesso.')
    setOpen(false)
  }

  function setStatus(appointment: Appointment, status: Appointment['status']) {
    updateData((current) => ({ ...current, appointments: current.appointments.map((item) => item.id === appointment.id ? { ...item, status } : item) }))
    setNotice(status === 'Concluída' ? 'Atendimento registrado no histórico do paciente.' : 'Consulta cancelada e mantida no histórico.')
  }

  const scheduled = data.appointments.filter((item) => item.status === 'Agendada').length
  const completed = data.appointments.filter((item) => item.status === 'Concluída').length
  const patients = [...data.patients].sort((a, b) => a.name.localeCompare(b.name))
  const professionals = [...data.professionals].sort((a, b) => a.name.localeCompare(b.name))

  return (
    <>
      <PageHeading eyebrow="AGENDA ASSISTENCIAL" title="Consultas" description="Agende atendimentos e acompanhe alterações e histórico da agenda." action={<button className="button" onClick={() => openForm()}><span className="button-plus">+</span> Agendar consulta</button>} />
      <section className="metric-grid compact-metrics">
        <article className="metric-card"><div className="metric-icon blue">▦</div><div className="metric-copy"><span>Consultas na agenda</span><strong>{data.appointments.length}</strong><small>Inclui histórico completo</small></div></article>
        <article className="metric-card"><div className="metric-icon gold">◷</div><div className="metric-copy"><span>Agendamentos abertos</span><strong>{scheduled}</strong><small>Aguardando atendimento</small></div></article>
        <article className="metric-card"><div className="metric-icon">✓</div><div className="metric-copy"><span>Atendimentos concluídos</span><strong>{completed}</strong><small>Salvos no histórico</small></div></article>
        <article className="metric-card"><div className="metric-icon violet">⚕</div><div className="metric-copy"><span>Profissionais na agenda</span><strong>{new Set(data.appointments.filter((item) => item.status === 'Agendada').map((item) => item.professionalId)).size}</strong><small>Com horários reservados</small></div></article>
      </section>
      {notice && <div className="inline-notice">{notice}<button className="notice-close" onClick={() => setNotice('')} aria-label="Fechar aviso">×</button></div>}
      {(data.patients.length === 0 || data.professionals.length === 0) && <div className="inline-notice error">Cadastre ao menos um paciente e um profissional antes de agendar.</div>}
      <section className="panel records-panel">
        <div className="panel-header records-header"><div><h2>Agenda de atendimentos</h2><p>Os horários são reservados por profissional.</p></div><SearchInput value={query} onChange={setQuery} placeholder="Buscar paciente ou profissional" /></div>
        <div className="list-toolbar"><div className="filter-tabs">{filters.map((value) => <button key={value} className={`filter-tab ${filter === value ? 'active' : ''}`} onClick={() => setFilter(value)}>{value}</button>)}</div><span className="list-count">{appointments.length} {appointments.length === 1 ? 'consulta' : 'consultas'}</span></div>
        <div className="table-wrap"><table className="data-table"><thead><tr><th>Paciente</th><th>Data e horário</th><th>Profissional</th><th>Tipo</th><th>Status</th><th aria-label="Ações" /></tr></thead><tbody>
          {appointments.map((appointment) => <tr key={appointment.id}><td><strong className="table-primary">{patientName(data, appointment.patientId)}</strong></td><td>{formatDate(appointment.date)} <span className="muted-separator">·</span> {appointment.time}</td><td>{professionalName(data, appointment.professionalId)}</td><td>{appointment.type}</td><td><StatusBadge>{appointment.status}</StatusBadge></td><td><div className="table-actions"><button className="button ghost small" onClick={() => openForm(appointment)}>Editar</button>{appointment.status === 'Agendada' && <><button className="button ghost small complete-link" onClick={() => setStatus(appointment, 'Concluída')}>Concluir</button><button className="button ghost small delete-link" onClick={() => setStatus(appointment, 'Cancelada')}>Cancelar</button></>}</div></td></tr>)}
          {appointments.length === 0 && <tr><td colSpan={6}><div className="empty-state"><strong>Nenhuma consulta nesta lista</strong>Agendamentos novos aparecerão aqui.</div></td></tr>}
        </tbody></table></div>
        <div className="table-footer"><span>Histórico preservado por paciente</span><span>Dados locais de demonstração</span></div>
      </section>
      {open && <Modal title={editing ? 'Editar consulta' : 'Agendar consulta'} description="Escolha o paciente, o profissional e um horário disponível." onClose={() => setOpen(false)}><form onSubmit={saveAppointment}><div className="form-grid">
        <label className="form-field full"><span>Paciente *</span><select name="patientId" defaultValue={editing?.patientId ?? ''} required><option value="" disabled>Selecione um paciente</option>{patients.map((patient) => <option key={patient.id} value={patient.id}>{patient.name}</option>)}</select></label>
        <label className="form-field full"><span>Profissional responsável *</span><select name="professionalId" defaultValue={editing?.professionalId ?? ''} required><option value="" disabled>Selecione um profissional</option>{professionals.map((person) => <option key={person.id} value={person.id}>{person.name} · {person.specialty}</option>)}</select></label>
        <label className="form-field"><span>Data *</span><input type="date" name="date" defaultValue={editing?.date ?? tomorrow()} min={today()} required /></label>
        <label className="form-field"><span>Horário *</span><input type="time" name="time" defaultValue={editing?.time ?? '09:00'} required /></label>
        <label className="form-field full"><span>Tipo de atendimento *</span><select name="type" defaultValue={editing?.type ?? 'Consulta'} required><option>Consulta</option><option>Retorno</option><option>Exame</option><option>Teleconsulta</option></select></label>
        <label className="form-field full"><span>Observações</span><textarea name="notes" defaultValue={editing?.notes} placeholder="Motivo do atendimento ou observações relevantes" /></label>
      </div>{error && <div className="inline-notice error">{error}</div>}<div className="form-actions"><button className="button secondary" type="button" onClick={() => setOpen(false)}>Cancelar</button><button className="button" type="submit" disabled={!patients.length || !professionals.length}>{editing ? 'Salvar alterações' : 'Confirmar agendamento'}</button></div></form></Modal>}
    </>
  )
}
