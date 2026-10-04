import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/pacientes')({ component: Pacientes })

function Pacientes() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Pacientes</h1>
      <p className="mt-4">Tela de pacientes.</p>
    </main>
  )
}
