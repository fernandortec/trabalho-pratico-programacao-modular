import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { makeId, Modal, PageHeading, patientName, roomOccupancy, useHospital, type Room } from '../hospital'

export const Route = createFileRoute('/quartos')({ component: Quartos })

const sectors = ['Clínica médica', 'Cirurgia', 'Pediatria', 'Maternidade', 'UTI']

function Quartos() {
  const { data, updateData } = useHospital()
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Room | null>(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const capacity = data.rooms.reduce((sum, room) => sum + room.capacity, 0)
  const occupied = data.rooms.reduce((sum, room) => sum + roomOccupancy(data, room.id), 0)
  const available = Math.max(0, capacity - occupied)
  const fullRooms = data.rooms.filter((room) => roomOccupancy(data, room.id) >= room.capacity).length

  function openForm(room?: Room) {
    setEditing(room ?? null)
    setError('')
    setOpen(true)
  }

  function saveRoom(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const room: Room = {
      id: editing?.id ?? makeId('r'),
      name: String(form.get('name')).trim().toUpperCase(),
      sector: String(form.get('sector')),
      capacity: Number(form.get('capacity')),
    }
    if (data.rooms.some((item) => item.id !== room.id && item.name.toLowerCase() === room.name.toLowerCase())) return setError('Já existe um quarto com esta identificação.')
    if (room.capacity < roomOccupancy(data, room.id)) return setError('A capacidade não pode ficar abaixo da ocupação atual deste quarto.')
    updateData((current) => ({
      ...current,
      rooms: editing
        ? current.rooms.map((item) => item.id === room.id ? room : item)
        : [...current.rooms, room],
    }))
    setNotice(editing ? 'Quarto atualizado.' : 'Quarto adicionado à unidade.')
    setOpen(false)
  }

  function removeRoom(room: Room) {
    if (data.hospitalizations.some((stay) => stay.roomId === room.id)) return setNotice('Este quarto está vinculado ao histórico de internações e não pode ser removido.')
    if (!window.confirm(`Remover o quarto ${room.name}?`)) return
    updateData((current) => ({ ...current, rooms: current.rooms.filter((item) => item.id !== room.id) }))
    setNotice('Quarto removido.')
  }

  return (
    <>
      <PageHeading eyebrow="ESTRUTURA HOSPITALAR" title="Quartos e leitos" description="Controle a capacidade dos quartos e veja a disponibilidade por setor." action={<button className="button" onClick={() => openForm()}><span className="button-plus">+</span> Novo quarto</button>} />
      <section className="metric-grid compact-metrics">
        <article className="metric-card"><div className="metric-icon blue">⌂</div><div className="metric-copy"><span>Quartos cadastrados</span><strong>{data.rooms.length}</strong><small>Na unidade central</small></div></article>
        <article className="metric-card"><div className="metric-icon">▦</div><div className="metric-copy"><span>Capacidade total</span><strong>{capacity}</strong><small>Leitos disponíveis no sistema</small></div></article>
        <article className="metric-card"><div className="metric-icon gold">▣</div><div className="metric-copy"><span>Leitos ocupados</span><strong>{occupied}</strong><small>{capacity ? Math.round(occupied / capacity * 100) : 0}% de ocupação</small></div></article>
        <article className="metric-card"><div className="metric-icon violet">✓</div><div className="metric-copy"><span>Vagas disponíveis</span><strong>{available}</strong><small>{fullRooms} {fullRooms === 1 ? 'quarto lotado' : 'quartos lotados'}</small></div></article>
      </section>
      {notice && <div className="inline-notice">{notice}<button className="notice-close" onClick={() => setNotice('')} aria-label="Fechar aviso">×</button></div>}
      <div className="rooms-section-heading"><div><h2>Mapa de quartos</h2><p>Ocupação calculada a partir das internações ativas.</p></div><span className="room-legend"><i /> Disponível <i className="legend-full" /> Lotado</span></div>
      <section className="room-grid">
        {data.rooms.map((room) => {
          const occupiedInRoom = roomOccupancy(data, room.id)
          const full = occupiedInRoom >= room.capacity
          const patients = data.hospitalizations.filter((stay) => stay.roomId === room.id && stay.status === 'Internado')
          const ratio = Math.min(100, Math.round(occupiedInRoom / room.capacity * 100))
          return <article className="room-card" key={room.id}>
            <div className="room-card-top"><div><h3>{room.name}</h3><p>{room.sector}</p></div><span className={`bed-count ${full ? 'bed-count-full' : ''}`}>{full ? 'Lotado' : `${room.capacity - occupiedInRoom} vaga(s)`}</span></div>
            <div className="room-capacity-line"><span>Ocupação</span><strong>{occupiedInRoom} / {room.capacity} leitos</strong></div>
            <div className="room-track"><span className={full ? 'full-fill' : ''} style={{ width: `${ratio}%` }} /></div>
            <div className="room-occupants">{patients.length ? patients.map((stay) => <div className="occupant-row" key={stay.id}><span className="occupant-dot" />{patientName(data, stay.patientId)}<small>Internado</small></div>) : <span className="vacant-copy">Nenhum paciente internado neste quarto</span>}</div>
            <div className="room-card-actions"><button className="button ghost small" onClick={() => openForm(room)}>Editar quarto</button><button className="button ghost small delete-link" onClick={() => removeRoom(room)}>Remover</button></div>
          </article>
        })}
        {data.rooms.length === 0 && <div className="panel room-empty"><div className="empty-state"><strong>Nenhum quarto cadastrado</strong>Adicione os quartos para controlar a disponibilidade de leitos.</div></div>}
      </section>
      {open && <Modal title={editing ? 'Editar quarto' : 'Novo quarto'} description="A capacidade não pode ficar abaixo do número de pacientes internados." onClose={() => setOpen(false)}><form onSubmit={saveRoom}><div className="form-grid">
        <label className="form-field"><span>Identificação *</span><input name="name" defaultValue={editing?.name} placeholder="Ex.: A-101" required autoFocus /></label>
        <label className="form-field"><span>Setor *</span><select name="sector" defaultValue={editing?.sector ?? ''} required><option value="" disabled>Selecione</option>{sectors.map((sector) => <option key={sector}>{sector}</option>)}</select></label>
        <label className="form-field"><span>Capacidade em leitos *</span><input type="number" min={Math.max(1, editing ? roomOccupancy(data, editing.id) : 1)} max="20" name="capacity" defaultValue={editing?.capacity ?? 2} required /></label>
      </div>{error && <div className="inline-notice error">{error}</div>}<div className="form-actions"><button className="button secondary" type="button" onClick={() => setOpen(false)}>Cancelar</button><button className="button" type="submit">{editing ? 'Salvar alterações' : 'Adicionar quarto'}</button></div></form></Modal>}
    </>
  )
}
