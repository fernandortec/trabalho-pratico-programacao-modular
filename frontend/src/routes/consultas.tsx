import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/consultas')({ component: Consultas })

function Consultas() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Consultas</h1>
      <p className="mt-4">Tela de consultas e agendamentos.</p>
    </main>
  )
}
