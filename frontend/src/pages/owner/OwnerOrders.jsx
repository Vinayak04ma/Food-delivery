import { useEffect, useState } from 'react';
import MainLayout from '../../layouts/MainLayout';
import StatusBadge from '../../components/common/StatusBadge';
import Button from '../../components/common/Button';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { formatCurrency, formatDateTime } from '../../utils/formatters';
import { ArrowLeft, Package } from 'lucide-react';
import { Link } from 'react-router-dom';
import orderService from '../../services/orderService';
import toast from 'react-hot-toast';

const STATUS_LABELS = {
  PENDING: { next: 'CONFIRMED', label: 'Accept Order', variant: 'primary' },
  CONFIRMED: { next: 'PREPARING', label: 'Start Preparing', variant: 'primary' },
  PREPARING: { next: 'OUT_FOR_DELIVERY', label: 'Dispatch Order', variant: 'primary' },
  OUT_FOR_DELIVERY: { next: 'DELIVERED', label: 'Mark Delivered', variant: 'primary' },
};

const OwnerOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');

  const loadOrders = async () => {
    try {
      const data = await orderService.getRestaurantOrders();
      setOrders(data || []);
    } catch {
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
    const interval = setInterval(loadOrders, 10000); // Polling for new orders
    return () => clearInterval(interval);
  }, []);

  const handleStatusUpdate = async (id, status) => {
    try {
      await orderService.updateStatus(id, { orderStatus: status });
      toast.success(`Order status updated to ${status.replace(/_/g, ' ')}`);
      loadOrders();
    } catch (err) {
      toast.error(err?.message || 'Failed to update status');
    }
  };

  const filteredOrders = orders.filter((order) => {
    if (filter === 'ALL') return true;
    if (filter === 'ACTIVE') return ['PENDING', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY'].includes(order.orderStatus);
    return order.orderStatus === filter;
  });

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
          <div className="flex items-center gap-3">
            <Link to="/owner" className="p-2 rounded-xl transition-colors hover:bg-[var(--color-bg-tertiary)]">
              <ArrowLeft className="w-5 h-5" style={{ color: 'var(--color-text-secondary)' }} />
            </Link>
            <h1 className="text-2xl font-black" style={{ color: 'var(--color-text-primary)' }}>
              <Package className="inline w-6 h-6 mr-2" /> Restaurant Orders
            </h1>
          </div>

          {/* Filter Pills */}
          <div className="flex items-center gap-2 overflow-x-auto pb-2 sm:pb-0">
            {['ALL', 'ACTIVE', 'PENDING', 'DELIVERED', 'CANCELLED'].map((f) => (
              <button
                key={f}
                onClick={() => setFilter(f)}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                  filter === f
                    ? 'bg-[var(--color-primary)] text-white shadow-sm'
                    : 'hover:bg-[var(--color-bg-tertiary)]'
                }`}
                style={{
                  color: filter === f ? '#fff' : 'var(--color-text-secondary)',
                  backgroundColor: filter === f ? 'var(--color-primary)' : 'var(--color-surface)',
                  border: '1px solid var(--color-border)',
                }}
              >
                {f}
              </button>
            ))}
          </div>
        </div>

        {loading ? (
          <LoadingSpinner text="Loading orders..." />
        ) : filteredOrders.length === 0 ? (
          <div className="text-center py-16">
            <p className="text-5xl mb-4">📦</p>
            <p className="font-bold text-lg" style={{ color: 'var(--color-text-primary)' }}>No orders found</p>
            <p className="text-sm" style={{ color: 'var(--color-text-secondary)' }}>
              {filter === 'ALL'
                ? 'Orders will appear here in real-time when customers order from your restaurant'
                : `No orders matching filter "${filter}"`}
            </p>
          </div>
        ) : (
          <div className="grid gap-4 stagger-children">
            {filteredOrders.map((order) => {
              const action = STATUS_LABELS[order.orderStatus];
              return (
                <div
                  key={order.id}
                  className="p-5 rounded-2xl transition-all hover:shadow-md"
                  style={{
                    backgroundColor: 'var(--color-surface)',
                    border: '1px solid var(--color-border)',
                  }}
                >
                  <div className="flex items-start justify-between mb-3">
                    <div>
                      <div className="flex items-center gap-2">
                        <p className="font-bold text-base" style={{ color: 'var(--color-text-primary)' }}>
                          #{order.orderNumber}
                        </p>
                        {order.orderStatus === 'PENDING' && (
                          <span className="animate-pulse px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-500 border border-amber-500/30">
                            New Order!
                          </span>
                        )}
                      </div>
                      <p className="text-xs" style={{ color: 'var(--color-text-tertiary)' }}>
                        Customer: <strong style={{ color: 'var(--color-text-secondary)' }}>{order.customerName || 'Customer'}</strong> • {formatDateTime(order.createdAt)}
                      </p>
                      {order.deliveryAddress && (
                        <p className="text-xs mt-1" style={{ color: 'var(--color-text-secondary)' }}>
                          📍 {order.deliveryAddress}
                        </p>
                      )}
                    </div>
                    <StatusBadge status={order.orderStatus} />
                  </div>

                  <div className="flex flex-wrap gap-2 text-xs mb-4" style={{ color: 'var(--color-text-secondary)' }}>
                    {order.items?.map((item) => (
                      <span
                        key={item.id}
                        className="px-2.5 py-1 rounded-lg font-medium"
                        style={{ backgroundColor: 'var(--color-bg-tertiary)' }}
                      >
                        {item.foodItemName} × {item.quantity} ({formatCurrency(item.price)})
                      </span>
                    ))}
                  </div>

                  <div className="flex items-center justify-between pt-3 border-t" style={{ borderColor: 'var(--color-border)' }}>
                    <div>
                      <span className="text-xs font-semibold block" style={{ color: 'var(--color-text-tertiary)' }}>Total</span>
                      <span className="font-bold text-lg" style={{ color: 'var(--color-text-primary)' }}>
                        {formatCurrency(order.totalAmount)}
                      </span>
                    </div>

                    <div className="flex items-center gap-2">
                      {order.orderStatus === 'PENDING' && (
                        <button
                          onClick={() => handleStatusUpdate(order.id, 'CANCELLED')}
                          className="px-3 py-1.5 rounded-xl text-xs font-semibold text-red-500 hover:bg-red-500/10 border border-red-500/30 transition-colors cursor-pointer"
                        >
                          Reject
                        </button>
                      )}
                      {action && (
                        <Button
                          size="sm"
                          onClick={() => handleStatusUpdate(order.id, action.next)}
                        >
                          {action.label}
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default OwnerOrders;
