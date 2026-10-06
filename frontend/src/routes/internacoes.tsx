import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { formatDate, makeId, Modal, PageHeading, patientName, roomName, roomOccupancy, SearchInput, StatusBadge, useHospital, type Hospitalization } from '../hospital'

export const Route = createFileRoute('/internacoes')({ component: Internacoes })

const today = () => new Date().toLocaleDateString('en-CA')
const filters = ['Todas', 'Internado', 'Alta'] as const

function Internacoes() {
  const { data, updateData } = useHospital()
  const [query, setQuery] = useState('')
  const [filter, setFilter] = useState<(typeof filters)[number]>('Todas')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Hospitalization | null>(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const stays = [...data.hospitalizations]
    .filter((stay) => filter === 'Todas' || stay.status === filter)
    .filter((stay) => `${patientName(data, stay.patientId)} ${roomName(data, stay.roomId)} ${stay.reason}`.toLowerCase().includes(query.toLowerCase()))
    .sort((a, b) => b.admittedAt.localeCompare(a.admittedAt))
  const occupied = data.hospitalizations.filter((stay) => stay.status === 'Internado').length
  const totalBeds = data.rooms.reduce((sum, room) => sum + room.capacity, 0)
  const availableBeds = Math.max(0, totalBeds - occupied)
  const activePatientIds = new Set(data.hospitalizations.filter((stay) => stay.status === 'Internado').map((stay) => stay.patientId))

  function openForm(stay?: Hospitalization) {
    setEditing(stay ?? null)
    setError('')
    setOpen(true)
  }

  function saveStay(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const stay: Hospitalization = {
      id: editing?.id ?? makeId('h'),
      patientId: String(form.get('patientId')),
      roomId: String(form.get('roomId')),
      admittedAt: String(form.get('admittedAt')),
      reason: String(form.get('reason')).trim(),
      status: editing?.status ?? 'Internado',
      dischargedAt: editing?.dischargedAt,
    }
    if (stay.admittedAt > today()) return setError('A data de entrada não pode estar no futuro.')
    if (data.hospitalizations.some((item) => item.id !== stay.id && item.patientId === stay.patientId && item.status === 'Internado')) {
      return setError('Este paciente já possui uma internação ativa.')
    }
    const room = data.rooms.find((item) => item.id === stay.roomId)
    const occupancy = room ? roomOccupancy(data, room.id) : 0
    if (!room || occupancy >= room.capacity && room.id !== editing?.roomId) return setError('Este quarto não tem leitos disponíveis. Escolha outro quarto.')
    updateData((current) => ({
      ...current,
      hospitalizations: editing
        ? current.hospitalizations.map((item) => item.id === stay.id ? stay : item)
        : [stay, ...current.hospitalizations],
    }))
    setNotice(editing ? 'Dados da internação atualizados.' : 'Internação registrada e leito reservado.')
    setOpen(false)
  }

  function discharge(stay: Hospitalization) {
    updateData((current) => ({
      ...current,
      hospitalizations: current.hospitalizations.map((item) => item.id === stay.id ? { ...item, status: 'Alta', dischargedAt: today() } : item),
    }))
    setNotice(`Alta registrada para ${patientName(data, stay.patientId)}. O leito voltou a ficar disponível.`)
  }

  return (
    <>
      <PageHeading eyebrow="GESTÃO DE LEITOS" title="Internações" description="Registre entradas e altas e acompanhe o uso dos leitos hospitalares." action={<button className="button" onClick={() => openForm()}><span className="button-plus">+</span> Nova internação</button>} />
      <section className="metric-grid compact-metrics">
        <article className="metric-card"><div className="metric-icon gold">▣</div><div className="metric-copy"><span>Pacientes internados</span><strong>{occupied}</strong><small>Internações em andamento</small></div></article>
        <article className="metric-card"><div className="metric-icon">⌂</div><div className="metric-copy"><span>Leitos disponíveis</span><strong>{availableBeds}</strong><small>De {totalBeds} leitos cadastrados</small></div></article>
        <article className="metric-card"><div className="metric-icon blue">✓</div><div className="metric-copy"><span>Altas registradas</span><strong>{data.hospitalizations.filter((stay) => stay.status === 'Alta').length}</strong><small>Histórico preservado</small></div></article>
        <article className="metric-card"><div className="metric-icon violet">⌕</div><div className="metric-copy"><span>Registros exibidos</span><strong>{stays.length}</strong><small>Internações e altas</small></div></article>
      </section>
      {notice && <div className="inline-notice">{notice}<button className="notice-close" onClick={() => setNotice('')} aria-label="Fechar aviso">×</button></div>}
      {data.rooms.length === 0 && <div className="inline-notice error">Cadastre um quarto antes de registrar uma internação.</div>}
      <section className="panel records-panel">
        <div className="panel-header records-header"><div><h2>Histórico de internações</h2><p>As altas liberam o leito sem apagar o registro do paciente.</p></div><SearchInput value={query} onChange={setQuery} placeholder="Buscar paciente, quarto ou motivo" /></div>
        <div className="list-toolbar"><div className="filter-tabs">{filters.map((value) => <button key={value} className={`filter-tab ${filter === value ? 'active' : ''}`} onClick={() => setFilter(value)}>{value}</button>)}</div><span className="list-count">{stays.length} {stays.length === 1 ? 'registro' : 'registros'}</span></div>
        <div className="table-wrap"><table className="data-table"><thead><tr><th>Paciente</th><th>Quarto</th><th>Entrada</th><th>Motivo</th><th>Status</th><th aria-label="Ações" /></tr></thead><tbody>
          {stays.map((stay) => <tr key={stay.id}><td><strong className="table-primary">{patientName(data, stay.patientId)}</strong></td><td><span className="room-code">{roomName(data, stay.roomId)}</span></td><td>{formatDate(stay.admittedAt)}{stay.dischargedAt && <small className="sub-cell">Alta em {formatDate(stay.dischargedAt)}</small>}</td><td>{stay.reason}</td><td><StatusBadge>{stay.status}</StatusBadge></td><td><div className="table-actions">{stay.status === 'Internado' && <><button className="button ghost small" onClick={() => openForm(stay)}>Editar</button><button className="button ghost small complete-link" onClick={() => discharge(stay)}>Registrar alta</button></>}</div></td></tr>)}
          {stays.length === 0 && <tr><td colSpan={6}><div className="empty-state"><strong>Nenhuma internação nesta lista</strong>Novos registros aparecerão aqui.</div></td></tr>}
        </tbody></table></div>
        <div className="table-footer"><span>Registros vinculados ao histórico do paciente</span><span>{occupied} leitos ocupados agora</span></div>
      </section>
      {open && <Modal title={editing ? 'Editar internação' : 'Registrar internação'} description="A ocupação do quarto será atualizada automaticamente." onClose={() => setOpen(false)}><form onSubmit={saveStay}><div className="form-grid">
        <label className="form-field full"><span>Paciente *</span><select name="patientId" defaultValue={editing?.patientId ?? ''} required><option value="" disabled>Selecione um paciente</option>{data.patients.map((patient) => <option key={patient.id} value={patient.id} disabled={activePatientIds.has(patient.id) && patient.id !== editing?.patientId}>{patient.name}{activePatientIds.has(patient.id) && patient.id !== editing?.patientId ? ' · já internado' : ''}</option>)}</select></label>
        <label className="form-field"><span>Quarto / leito *</span><select name="roomId" defaultValue={editing?.roomId ?? ''} required><option value="" disabled>Selecione um quarto</option>{data.rooms.map((room) => { const occupiedInRoom = roomOccupancy(data, room.id); const full = occupiedInRoom >= room.capacity && room.id !== editing?.roomId; return <option key={room.id} value={room.id} disabled={full}>{room.name} · {room.sector} · {full ? 'lotado' : `${room.capacity - occupiedInRoom} vaga(s)`}</option> })}</select></label>
        <label className="form-field"><span>Data de entrada *</span><input type="date" name="admittedAt" defaultValue={editing?.admittedAt ?? today()} max={today()} required /></label>
        <label className="form-field full"><span>Motivo da internação *</span><input name="reason" defaultValue={editing?.reason} placeholder="Ex.: Observação clínica" required /></label>
      </div>{error && <div className="inline-notice error">{error}</div>}<div className="form-actions"><button className="button secondary" type="button" onClick={() => setOpen(false)}>Cancelar</button><button className="button" type="submit" disabled={!data.rooms.length || !data.patients.length}>{editing ? 'Salvar alterações' : 'Confirmar internação'}</button></div></form></Modal>}
    </>
  )
}
