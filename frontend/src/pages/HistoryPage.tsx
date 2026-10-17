import { useState, useEffect, useCallback } from 'react';
import { Loader2, ClipboardList, ChevronDown, ChevronUp } from 'lucide-react';
import toast from 'react-hot-toast';
import { getDispenseHistory } from '../api/drugs';
import type { DispensedDrugs } from '../types';

function formatDateTime(iso: string): string {
  try {
    return new Date(iso).toLocaleString('en-NG', {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  } catch {
    return iso;
  }
}

export default function HistoryPage() {
  const [records, setRecords] = useState<DispensedDrugs[]>([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState<Set<string>>(new Set());

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setRecords(await getDispenseHistory());
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Failed to load history');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const toggleRow = (id: string) => {
    setExpanded((prev) => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  };

  const totalRevenue = records.reduce(
    (sum, r) => sum + r.items.reduce((s, i) => s + i.totalPrice, 0),
    0
  );

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Dispensing History</h1>
          <p className="page-subtitle">{records.length} transaction{records.length !== 1 ? 's' : ''}</p>
        </div>
        {records.length > 0 && (
          <div className="stat-chip">
            Total revenue: <strong>₦{totalRevenue.toLocaleString()}</strong>
          </div>
        )}
      </div>

      {loading ? (
        <div className="center-spinner"><Loader2 size={32} className="spin" /></div>
      ) : records.length === 0 ? (
        <div className="empty-state">
          <ClipboardList size={48} strokeWidth={1} />
          <p>No dispensing records yet</p>
        </div>
      ) : (
        <div className="history-list">
          {records.map((record) => {
            const isOpen = expanded.has(record.id);
            const recordTotal = record.items.reduce((s, i) => s + i.totalPrice, 0);
            return (
              <div key={record.id} className="history-card card">
                <button
                  className="history-card-header"
                  onClick={() => toggleRow(record.id)}
                  aria-expanded={isOpen}
                >
                  <div className="history-card-left">
                    <span className="history-date">{formatDateTime(record.saleDateTime)}</span>
                    <span className="history-by">by {record.dispensedBy}</span>
                  </div>
                  <div className="history-card-right">
                    <span className="history-amount">₦{recordTotal.toLocaleString()}</span>
                    <span className="history-count">{record.items.length} item{record.items.length !== 1 ? 's' : ''}</span>
                    {isOpen ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                  </div>
                </button>

                {isOpen && (
                  <div className="history-items">
                    <table className="data-table data-table--compact">
                      <thead>
                        <tr>
                          <th>Drug</th>
                          <th>Qty</th>
                          <th>Total (₦)</th>
                        </tr>
                      </thead>
                      <tbody>
                        {record.items.map((item, i) => (
                          <tr key={i}>
                            <td>{item.drugName}</td>
                            <td>{item.quantity}</td>
                            <td>₦{item.totalPrice.toLocaleString()}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
